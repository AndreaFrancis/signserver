/*************************************************************************
 *                                                                       *
 *  SignServer: The OpenSource Automated Signing Server                  *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.signserver.server.cryptotokens;

import org.signserver.common.UnsupportedCryptoTokenParameter;
import org.signserver.common.NoSuchAliasException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.cesecore.keys.token.CryptoTokenAuthenticationFailedException;
import org.cesecore.keys.token.p11.Pkcs11SlotLabelType;
import org.cesecore.keys.token.p11.exception.NoSuchSlotException;
import org.cesecore.util.query.QueryCriteria;
import org.signserver.common.CryptoTokenAuthenticationFailureException;
import org.signserver.common.CryptoTokenInitializationFailureException;
import org.signserver.common.CryptoTokenOfflineException;
import org.signserver.common.PKCS11Settings;
import org.signserver.common.ICertReqData;
import org.signserver.common.ISignerCertReqInfo;
import org.signserver.common.IllegalRequestException;
import org.signserver.common.KeyTestResult;
import org.signserver.common.QueryException;
import org.signserver.common.RequestContext;
import org.signserver.common.SignServerException;
import org.signserver.common.TokenOutOfSpaceException;
import org.signserver.common.WorkerStatus;
import static org.signserver.server.BaseProcessable.PROPERTY_CACHE_PRIVATEKEY;
import org.signserver.server.ExceptionUtil;
import org.signserver.server.IServices;

/**
 * CryptoToken implementation wrapping the new PKCS11CryptoToken from CESeCore.
 * 
 * Note: The mapping between SignServer APIs and CESeCore is not perfect. In 
 * particular the SignServer calls for testing and generating key-pairs takes 
 * an authentication code while the CESeCore ones assumes the token is already 
 * activated. This means that the auth code parameter will be ignored for those
 * methods.
 *
 * @author Markus Kilås
 * @version $Id$
 */
public class PKCS11CryptoToken extends BaseCryptoToken {

    private static final Logger LOG = Logger.getLogger(PKCS11CryptoToken.class);

    private final KeyStorePKCS11CryptoToken delegate;

    public PKCS11CryptoToken() throws InstantiationException {
        delegate = new KeyStorePKCS11CryptoToken();
    }

    private String keyAlias;
    private String nextKeyAlias;

    private boolean cachePrivateKey;
    private PrivateKey cachedPrivateKey;
    
    // cached P11 library definitions (defined at deploy-time)
    private PKCS11Settings settings;
    
    private Integer keygenerationLimit;

    @Override
    public void init(int workerId, Properties props) throws CryptoTokenInitializationFailureException {
        try {
            final String attributesValue = props.getProperty(CryptoTokenHelper.PROPERTY_ATTRIBUTES);
            if (attributesValue != null && props.getProperty(CryptoTokenHelper.PROPERTY_ATTRIBUTESFILE) != null) {
                throw new CryptoTokenInitializationFailureException(
                        "Only specify one of " + CryptoTokenHelper.PROPERTY_ATTRIBUTES
                                + " and " + CryptoTokenHelper.PROPERTY_ATTRIBUTESFILE);
            }

            if (attributesValue != null) {
                OutputStream out = null;
                try {
                    File attributesFile = File.createTempFile("attributes-" + workerId + "-", ".tmp");
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Created attributes file: " + attributesFile.getAbsolutePath());
                    }
                    attributesFile.deleteOnExit();
                    out = new FileOutputStream(attributesFile);
                    IOUtils.write(attributesValue, out);
                    props.setProperty(CryptoTokenHelper.PROPERTY_ATTRIBUTESFILE, attributesFile.getAbsolutePath());
                } catch (IOException ex) {
                    throw new CryptoTokenInitializationFailureException("Unable to create attributes file", ex);
                } finally {
                    IOUtils.closeQuietly(out);
                }
            }

            // Check that both the new or the legacy properties are specified at the same time
            if (props.getProperty(CryptoTokenHelper.PROPERTY_SLOT) != null && props.getProperty(CryptoTokenHelper.PROPERTY_SLOTLABELVALUE) != null) {
                throw new CryptoTokenInitializationFailureException("Can not specify both " + CryptoTokenHelper.PROPERTY_SLOT + " and  " + CryptoTokenHelper.PROPERTY_SLOTLABELVALUE);
            }
            if (props.getProperty(CryptoTokenHelper.PROPERTY_SLOTLISTINDEX) != null && props.getProperty(CryptoTokenHelper.PROPERTY_SLOTLABELVALUE) != null) {
                throw new CryptoTokenInitializationFailureException("Can not specify both " + CryptoTokenHelper.PROPERTY_SLOTLISTINDEX + " and  " + CryptoTokenHelper.PROPERTY_SLOTLABELVALUE);
            }

            props = CryptoTokenHelper.fixP11Properties(props);
            
            final String sharedLibraryName = props.getProperty("sharedLibraryName");
            final String sharedLibraryProperty = props.getProperty("sharedLibrary");
            
            settings = PKCS11Settings.getInstance();

            // at least one the SHAREDLIBRARYNAME or SHAREDLIBRAY
            // (for backwards compatability) properties must be defined
            if (sharedLibraryName == null && sharedLibraryProperty == null) {
                final StringBuilder sb = new StringBuilder();
                
                sb.append("Missing SHAREDLIBRARYNAME property\n");
                settings.listAvailableLibraryNames(sb);
                
                throw new CryptoTokenInitializationFailureException(sb.toString());
            }

            // if only the old SHAREDLIBRARY property is given, it must point
            // to one of the libraries defined at deploy-time
            if (sharedLibraryProperty != null && sharedLibraryName == null) {
                // check if the library was defined at deploy-time
                if (!settings.isP11LibraryExisting(sharedLibraryProperty)) {
                    throw new CryptoTokenInitializationFailureException("SHAREDLIBRARY is not permitted when pointing to a library not defined at deploy-time");
                }
            }
            
            // lookup the library defined by SHAREDLIBRARYNAME among the
            // deploy-time-defined values
            final String sharedLibraryFile =
                    sharedLibraryName == null ?
                    null :
                    settings.getP11SharedLibraryFileForName(sharedLibraryName);
            
            // both the old and new properties are allowed at the same time
            // to ease migration, given that they point to the same library
            if (sharedLibraryProperty != null && sharedLibraryName != null) {
                if (sharedLibraryFile != null) {
                    final File byPath = new File(sharedLibraryProperty);
                    final File byName = new File(sharedLibraryFile);

                    try {
                        if (!byPath.getCanonicalPath().equals(byName.getCanonicalPath())) {
                            // the properties pointed to different libraries
                            throw new CryptoTokenInitializationFailureException("Can not specify both SHAREDLIBRARY and SHAREDLIBRARYNAME at the same time");
                        }
                    } catch (IOException e) {
                        // failed to determine canonical paths, treat this as conflicting properties
                        throw new CryptoTokenInitializationFailureException("Can not specify both SHAREDLIBRARY and SHAREDLIBRARYNAME at the same time");
                    }
                } else {
                    // could not associate SHAREDLIBRARYNAME with a path, treat this as conflicting properties
                    throw new CryptoTokenInitializationFailureException("Can not specify both SHAREDLIBRARY and SHAREDLIBRARYNAME at the same time");
                }
            }
            
            // if only SHAREDLIBRARYNAME was given and the value couldn't be
            // found, include a list of available values in the token error
            // message
            if (sharedLibraryFile == null && sharedLibraryProperty == null) {
                final StringBuilder sb = new StringBuilder();
                
                sb.append("SHAREDLIBRARYNAME ");
                sb.append(sharedLibraryName);
                sb.append(" is not referring to a defined value");
                sb.append("\n");
                settings.listAvailableLibraryNames(sb);

                throw new CryptoTokenInitializationFailureException(sb.toString());
            }
            
            // check the file (again) and pass it on to the underlaying implementation
            if (sharedLibraryFile != null) {
                final File sharedLibrary = new File(sharedLibraryFile);
                if (!sharedLibrary.isFile() || !sharedLibrary.canRead()) {
                    throw new CryptoTokenInitializationFailureException("The shared library file can't be read: " + sharedLibrary.getAbsolutePath());
                }

                // propagate the shared library property to the delegate
                props.setProperty("sharedLibrary", sharedLibraryFile);
            }

            final String slotLabelType = props.getProperty(CryptoTokenHelper.PROPERTY_SLOTLABELTYPE);
            if (slotLabelType == null) {
                throw new CryptoTokenInitializationFailureException("Missing " + CryptoTokenHelper.PROPERTY_SLOTLABELTYPE + " property");
            }
            final Pkcs11SlotLabelType slotLabelTypeValue =
                    Pkcs11SlotLabelType.getFromKey(slotLabelType);
            if (slotLabelTypeValue == null) {
                throw new CryptoTokenInitializationFailureException("Illegal " +
                        CryptoTokenHelper.PROPERTY_SLOTLABELTYPE + " property: " +
                        slotLabelType);
            }
            final String slotLabelValue = props.getProperty(CryptoTokenHelper.PROPERTY_SLOTLABELVALUE);
            if (slotLabelValue == null) {
                throw new CryptoTokenInitializationFailureException("Missing " + CryptoTokenHelper.PROPERTY_SLOTLABELVALUE + " property");
            }
            
            delegate.init(props, null, workerId);

            keyAlias = props.getProperty("defaultKey");
            nextKeyAlias = props.getProperty("nextCertSignKey");

            cachePrivateKey = Boolean.parseBoolean(props.getProperty(PROPERTY_CACHE_PRIVATEKEY, Boolean.FALSE.toString()));

            if (LOG.isDebugEnabled()) { 
                final StringBuilder sb = new StringBuilder();
                sb.append("keyAlias: ").append(keyAlias).append("\n");
                sb.append("nextKeyAlias: ").append(nextKeyAlias).append("\n");
                sb.append("cachePrivateKey: ").append(cachePrivateKey);
                LOG.debug(sb.toString());
            }

            // Read property KEYGENERATIONLIMIT
            final String keygenLimitValue = props.getProperty(CryptoTokenHelper.PROPERTY_KEYGENERATIONLIMIT);
            if (keygenLimitValue != null && !keygenLimitValue.trim().isEmpty()) {
                try {
                    keygenerationLimit = Integer.parseInt(keygenLimitValue.trim());
                } catch (NumberFormatException ex) {
                    throw new CryptoTokenInitializationFailureException("Incorrect value for " + CryptoTokenHelper.PROPERTY_KEYGENERATIONLIMIT + ": " + ex.getLocalizedMessage());
                }
            }
        } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
            LOG.error("Init failed", ex);
            throw new CryptoTokenInitializationFailureException(ex.getMessage());
        } catch (NoSuchSlotException ex) {
            LOG.error("Slot not found", ex);
            throw new CryptoTokenInitializationFailureException(ex.getMessage());
        } catch (NumberFormatException ex) {
            LOG.error("Init failed", ex);
            throw new CryptoTokenInitializationFailureException(ex.getMessage());
        }
    }

    @Override
    public int getCryptoTokenStatus(IServices services) {
        return getCryptoTokenStatus();
    }

    @Override
    public int getCryptoTokenStatus() {
        int result = delegate.getTokenStatus();

        if (result == WorkerStatus.STATUS_ACTIVE) {
            result = WorkerStatus.STATUS_OFFLINE;
            try {
                if (LOG.isDebugEnabled()) { 
                    final StringBuilder sb = new StringBuilder();
                    sb.append("keyAlias: ").append(keyAlias).append("\n");
                    sb.append("nextKeyAlias: ").append(nextKeyAlias).append("\n");
                    LOG.debug(sb.toString());
                }
                for (String testKey : new String[]{keyAlias, nextKeyAlias}) {
                    if (testKey != null && !testKey.isEmpty()) {
                        PrivateKey privateKey = delegate.getPrivateKey(testKey);
                        if (privateKey != null) {
                            PublicKey publicKey = delegate.getPublicKey(testKey);
                            CryptoTokenHelper.testSignAndVerify(privateKey, publicKey, delegate.getSignProviderName());
                            result = WorkerStatus.STATUS_ACTIVE;
                        }
                    }
                }
            } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
                LOG.error("Error testing activation", ex);
            } catch (NoSuchAlgorithmException ex) {
                LOG.error("Error testing activation", ex);
            } catch (NoSuchProviderException ex) {
                LOG.error("Error testing activation", ex);
            } catch (InvalidKeyException ex) {
                LOG.error("Error testing activation", ex);
            } catch (SignatureException ex) {
                LOG.error("Error testing activation", ex);
            }
        }

        return result;
    }

    @Override
    public void activate(String authenticationcode) throws CryptoTokenAuthenticationFailureException, CryptoTokenOfflineException {
        try {
            delegate.activate(authenticationcode.toCharArray());
        } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
            LOG.error("Activate failed", ex);
            throw new CryptoTokenOfflineException(ex);
        } catch (CryptoTokenAuthenticationFailedException ex) {
            
            final StringBuilder sb = new StringBuilder();
            sb.append("Activate failed");
            for (final String causeMessage : ExceptionUtil.getCauseMessages(ex)) {
                sb.append(": ");
                sb.append(causeMessage);
            }
            LOG.error(sb.toString());
            throw new CryptoTokenAuthenticationFailureException(sb.toString());
        }
    }

    @Override
    public boolean deactivate() throws CryptoTokenOfflineException {
        delegate.deactivate();
        return true;
    }

    @Override
    public PrivateKey getPrivateKey(int purpose) throws CryptoTokenOfflineException {
        final PrivateKey result;
        if (purpose == ICryptoToken.PURPOSE_NEXTKEY) {
            result = getPrivateKey(nextKeyAlias);
        } else {
            if (cachePrivateKey && cachedPrivateKey != null) {
                result = cachedPrivateKey;
            } else {
                result = getPrivateKey(keyAlias);
                if (cachePrivateKey) {
                    cachedPrivateKey = result;
                }
            }
        }
        return result;
    }

    @Override
    public PublicKey getPublicKey(int purpose) throws CryptoTokenOfflineException {
        final String alias = purpose == ICryptoToken.PURPOSE_NEXTKEY ? nextKeyAlias : keyAlias;
        return getPublicKey(alias);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) throws CryptoTokenOfflineException {
        try {
            return delegate.getPrivateKey(alias);
        } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
            throw new CryptoTokenOfflineException(ex);
        }
    }

    @Override
    public PublicKey getPublicKey(String alias) throws CryptoTokenOfflineException {
        try {
            return delegate.getPublicKey(alias);
        } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
            throw new CryptoTokenOfflineException(ex);
        }
    }

    @Override
    public String getProvider(int providerUsage) {
        return delegate.getSignProviderName();
    }

    @Override
    public Certificate getCertificate(int purpose) throws CryptoTokenOfflineException {
        final String alias = purpose == ICryptoToken.PURPOSE_NEXTKEY ? nextKeyAlias : keyAlias;
        return getCertificate(alias);
    }

    @Override
    public List<Certificate> getCertificateChain(int purpose) throws CryptoTokenOfflineException {
        final String alias = purpose == ICryptoToken.PURPOSE_NEXTKEY ? nextKeyAlias : keyAlias;
        return getCertificateChain(alias);
    }

    @Override
    public Certificate getCertificate(String alias) throws CryptoTokenOfflineException {
        try {
            Certificate result = delegate.getActivatedKeyStore().getCertificate(alias);
            
            // Do not return the dummy certificate
            if (CryptoTokenHelper.isDummyCertificate(result)) {
                result = null;
            }
            return result;
        } catch (KeyStoreException ex) {
            throw new CryptoTokenOfflineException(ex);
        }
    }

    @Override
    public List<Certificate> getCertificateChain(String alias) throws CryptoTokenOfflineException {
        try {
            final List<Certificate> result;
            final Certificate[] certChain = delegate.getActivatedKeyStore().getCertificateChain(alias);
            if (certChain == null || (certChain.length == 1 && CryptoTokenHelper.isDummyCertificate(certChain[0]))) {
                result = null;
            } else {
                result = Arrays.asList(certChain);
            }
            return result;
        } catch (KeyStoreException ex) {
            throw new CryptoTokenOfflineException(ex);
        }
    }

    @Override
    public ICertReqData genCertificateRequest(ISignerCertReqInfo info,
            final boolean explicitEccParameters, boolean defaultKey)
            throws CryptoTokenOfflineException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("defaultKey: " + defaultKey);
        }
        final String alias;
        if (defaultKey) {
            alias = keyAlias;
        } else {
            alias = nextKeyAlias;
        }
        return genCertificateRequest(info, explicitEccParameters, alias);
    }

    @Override
    public ICertReqData genCertificateRequest(ISignerCertReqInfo info,
            final boolean explicitEccParameters, String alias)
            throws CryptoTokenOfflineException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(">genCertificateRequest CESeCorePKCS11CryptoToken");
            LOG.debug("alias: " + alias);
        }
        try {
            return CryptoTokenHelper.genCertificateRequest(info, delegate.getPrivateKey(alias), getProvider(ICryptoToken.PROVIDERUSAGE_SIGN), delegate.getPublicKey(alias), explicitEccParameters);
        } catch (org.cesecore.keys.token.CryptoTokenOfflineException e) {
            LOG.error("Certificate request error: " + e.getMessage(), e);
            throw new CryptoTokenOfflineException(e);
        } catch (IllegalArgumentException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Certificate request error", ex);
            }
            throw new CryptoTokenOfflineException(ex.getMessage(), ex);
        }
    }

    /**
     * Method not supported.
     */
    @Override
    public boolean destroyKey(int purpose) {
        return false;
    }

    @Override
    public boolean removeKey(String alias) throws CryptoTokenOfflineException, KeyStoreException, SignServerException {
        return CryptoTokenHelper.removeKey(getKeyStore(), alias);
    }

    @Override
    public Collection<KeyTestResult> testKey(String alias, char[] authCode) throws CryptoTokenOfflineException, KeyStoreException {
        final KeyStore keyStore = delegate.getActivatedKeyStore();
        return CryptoTokenHelper.testKey(keyStore, alias, authCode, keyStore.getProvider().getName());
    }
    
    @Override
    public Collection<KeyTestResult> testKey(String alias, char[] authCode, IServices services) throws CryptoTokenOfflineException, KeyStoreException {
        return testKey(alias, authCode);
    }

    @Override
    public KeyStore getKeyStore() throws UnsupportedOperationException, CryptoTokenOfflineException, KeyStoreException {
        return delegate.getActivatedKeyStore();
    }

    @Override
    public void generateKey(String keyAlgorithm, String keySpec, String alias, char[] authCode) throws TokenOutOfSpaceException, CryptoTokenOfflineException, IllegalArgumentException {
        if (keySpec == null) {
            throw new IllegalArgumentException("Missing keyspec parameter");
        }
        if (alias == null) {
            throw new IllegalArgumentException("Missing alias parameter");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("keyAlgorithm: " + keyAlgorithm + ", keySpec: " + keySpec
                    + ", alias: " + alias);
        }
        // Keyspec for DSA is prefixed with "dsa"
        if (keyAlgorithm != null && keyAlgorithm.equalsIgnoreCase("DSA")
                && !keySpec.contains("dsa")) {
            keySpec = "dsa" + keySpec;
        }

        // Check key generation limit, if configured
        if (keygenerationLimit != null && keygenerationLimit > -1) {
            final int current;
            try {
                current = delegate.getActivatedKeyStore().size();
                if (current >= keygenerationLimit) {
                    throw new TokenOutOfSpaceException("Key generation limit exceeded: " + current);
                }
            } catch (KeyStoreException ex) {
                LOG.error("Checking key generation limit failed", ex);
                throw new TokenOutOfSpaceException("Current number of key entries could not be obtained: " + ex.getMessage(), ex);
            }
        }
        
        try {
            delegate.generateKeyPair(keySpec, alias);
        } catch (InvalidAlgorithmParameterException ex) {
            LOG.error(ex, ex);
            throw new CryptoTokenOfflineException(ex);
        } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
            LOG.error(ex, ex);
            throw new CryptoTokenOfflineException(ex);
        }
    }
    
    @Override
    public void generateKey(String keyAlgorithm, String keySpec, String alias, char[] authCode, Map<String, Object> params, IServices services) throws CryptoTokenOfflineException, IllegalArgumentException {
        generateKey(keyAlgorithm, keySpec, alias, authCode);
    }

    @Override
    public void importCertificateChain(final List<Certificate> certChain,
                                       final String alias,
                                       final char[] athenticationCode,
                                       final Map<String, Object> params,
                                       final IServices services)
            throws CryptoTokenOfflineException {
        try {
            final KeyStore keyStore = delegate.getActivatedKeyStore();
            final Key key = keyStore.getKey(alias, athenticationCode);
            
            CryptoTokenHelper.ensureNewPublicKeyMatchesOld(keyStore, alias, certChain.get(0));

            keyStore.setKeyEntry(alias, key, athenticationCode,
                                 certChain.toArray(new Certificate[0]));
        } catch (KeyStoreException ex) {
            LOG.error(ex, ex);
            throw new CryptoTokenOfflineException(ex);
        } catch (NoSuchAlgorithmException ex) {
            LOG.error(ex, ex);
            throw new CryptoTokenOfflineException(ex);
        } catch (UnrecoverableKeyException ex) {
            LOG.error(ex, ex);
            throw new CryptoTokenOfflineException(ex);
        }
    }

    @Override
    public TokenSearchResults searchTokenEntries(final int startIndex, final int max, final QueryCriteria qc, final boolean includeData, Map<String, Object> params, final IServices services) throws CryptoTokenOfflineException, QueryException {
        try {
            return CryptoTokenHelper.searchTokenEntries(getKeyStore(), startIndex, max, qc, includeData);
        } catch (KeyStoreException ex) {
            throw new CryptoTokenOfflineException(ex);
        }
    }

    @Override
    public ICryptoInstance acquireCryptoInstance(String alias, Map<String, Object> params, RequestContext context) throws
            CryptoTokenOfflineException, 
            NoSuchAliasException, 
            InvalidAlgorithmParameterException,
            UnsupportedCryptoTokenParameter,
            IllegalRequestException {
        final PrivateKey privateKey = getPrivateKey(alias);
        final List<Certificate> certificateChain = getCertificateChain(alias);
        return new DefaultCryptoInstance(alias, context, delegate.getActivatedKeyStore().getProvider(), privateKey, certificateChain);
    }

    @Override
    public void releaseCryptoInstance(ICryptoInstance instance, RequestContext context) {
        // NOP
    }

    @Override
    public ICertReqData genCertificateRequest(ISignerCertReqInfo info, boolean explicitEccParameters, String keyAlias, IServices services) throws CryptoTokenOfflineException {
        return genCertificateRequest(info, explicitEccParameters, keyAlias);
    }

    private static class KeyStorePKCS11CryptoToken extends org.cesecore.keys.token.PKCS11CryptoToken {

        public KeyStorePKCS11CryptoToken() throws InstantiationException {
            super();
        }

        public KeyStore getActivatedKeyStore() throws CryptoTokenOfflineException {
            try {
                return getKeyStore();
            } catch (org.cesecore.keys.token.CryptoTokenOfflineException ex) {
                throw new CryptoTokenOfflineException(ex);
            }
        }
    }

}
