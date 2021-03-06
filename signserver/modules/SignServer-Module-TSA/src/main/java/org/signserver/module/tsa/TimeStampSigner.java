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
package org.signserver.module.tsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cmp.PKIStatus;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.signserver.module.tsa.bc.TimeStampRequest;
import org.signserver.module.tsa.bc.TimeStampResponseGenerator;
import org.signserver.module.tsa.bc.TimeStampTokenGenerator;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.cesecore.util.Base64;
import org.signserver.common.*;
import org.signserver.server.ITimeSource;
import org.signserver.server.WorkerContext;
import org.signserver.server.archive.Archivable;
import org.signserver.server.archive.DefaultArchivable;
import org.signserver.server.cryptotokens.ICryptoInstance;
import org.signserver.server.cryptotokens.ICryptoToken;
import org.signserver.server.log.IWorkerLogger;
import org.signserver.server.log.LogMap;
import org.signserver.server.signers.BaseSigner;

/**
 * A Signer signing Time-stamp request according to RFC 3161 using the
 * BouncyCastle TimeStamp API.
 *
 * Implements a ISigner and have the following properties:
 *
 * <table border="1">
 *  <tr>
 *      <td>TIMESOURCE</td>
 *      <td>
 *          property containing the classpath to the ITimeSource implementation
 *          that should be used. (default LocalComputerTimeSource)
 *      </td>
 *  </tr>
 *  <tr>
 *      <td>ACCEPTEDALGORITHMS</td>
 *      <td>
 *          A ';' separated string containing accepted algorithms, can be null
 *          if it shouldn't be used. (OPTIONAL)
 *      </td>
 *  </tr>
 *  <tr>
 *      <td>ACCEPTEDPOLICIES</td>
 *      <td>
 *          A ';' separated string containing accepted policies, can be null if
 *          it shouldn't be used. (OPTIONAL)
 *      </td>
 * </tr>
 *  <tr>
 *      <td>ACCEPTEDEXTENSIONS</td>
 *      <td>
 *          A ';' separated string containing accepted extensions, can be null
 *          if it shouldn't be used. (OPTIONAL)
 *      </td>
 * </tr>
 *  <tr>
 *      <td>DIGESTOID</td>
 *      <td>
 *          The Digenst OID to be used in the timestamp
 *      </td>
 * </tr>
 *  <tr>
 *      <td>DEFAULTTSAPOLICYOID</td>
 *      <td>
 *          The default policy ID of the time stamp authority
 *      </td>
 * </tr>
 *  <tr>
 *      <td>ACCURACYMICROS</td>
 *      <td>
 *          Accuraty in micro seconds, Only decimal number format, only one of
 *          the accuracy properties should be set (OPTIONAL)
 *      </td>
 * </tr>
 *  <tr>
 *      <td>ACCURACYMILLIS</td>
 *      <td>
 *          Accuraty in milli seconds, Only decimal number format, only one of
 *          the accuracy properties should be set (OPTIONAL)
 *      </td>
 * </tr>
 *  <tr>
 *      <td>ACCURACYSECONDS</td>
 *      <td>
 *          Accuraty in seconds. Only decimal number format, only one of the
 *          accuracy properties should be set (OPTIONAL)
 *      </td>
 * </tr>
 *  <tr>
 *      <td>ORDERING</td>
 *      <td>
 *          The ordering (OPTIONAL), default false.
 *      </td>
 * </tr>
 *  <tr>
 *      <td>TSA</td>
 *      <td>
 *          General name of the Time Stamp Authority.
 *      </td>
 *  </tr>
 * <tr>
 *      <td>REQUIREVALIDCHAIN</td>
 *      <td>
 *          Set to true to perform an extra check that the SIGNERCERTCHAIN only 
 *          contains certificates in the chain of the signer certificate.
 *          (OPTIONAL), default false.
 *      </td>
 * </tr>
 *
 * </table>
 * 
 * Specifying a signer certificate (normally the SIGNERCERT property) is required 
 * as information from that certificate will be used to indicate which signer
 * signed the time-stamp token.
 * 
 * The SIGNERCERTCHAIN property contains all certificates included in the token 
 * if the client requests the certificates. The RFC specified that the signer 
 * certificate MUST be included in the list returned.
 * 
 *
 * @author philip
 * @version $Id$
 */
public class TimeStampSigner extends BaseSigner {

    /** Log4j instance for actual implementation class. */
    private static final Logger LOG = Logger.getLogger(TimeStampSigner.class);

    /** Random generator algorithm. */
    private static String algorithm = "SHA1PRNG";

    /** Random generator. */
    private transient SecureRandom random;

    /** MIME type for the request data. **/
    private static final String REQUEST_CONTENT_TYPE = "application/timestamp-query";
    
    /** MIME type for the response data. **/
    private static final String RESPONSE_CONTENT_TYPE = "application/timestamp-reply";

    // Property constants
    public static final String TIMESOURCE = "TIMESOURCE";
    public static final String SIGNATUREALGORITHM = "SIGNATUREALGORITHM";
    public static final String ACCEPTEDALGORITHMS = "ACCEPTEDALGORITHMS";
    public static final String ACCEPTEDPOLICIES = "ACCEPTEDPOLICIES";
    public static final String ACCEPTEDEXTENSIONS = "ACCEPTEDEXTENSIONS";
    //public static final String DEFAULTDIGESTOID    = "DEFAULTDIGESTOID";
    public static final String DEFAULTTSAPOLICYOID = "DEFAULTTSAPOLICYOID";
    public static final String ACCURACYMICROS = "ACCURACYMICROS";
    public static final String ACCURACYMILLIS = "ACCURACYMILLIS";
    public static final String ACCURACYSECONDS = "ACCURACYSECONDS";
    public static final String ORDERING = "ORDERING";
    public static final String INCLUDEORDERING = "INCLUDEORDERING";
    public static final String TSA = "TSA";
    public static final String TSA_FROM_CERT = "TSA_FROM_CERT";
    public static final String REQUIREVALIDCHAIN = "REQUIREVALIDCHAIN";
    public static final String MAXSERIALNUMBERLENGTH = "MAXSERIALNUMBERLENGTH";
    public static final String INCLUDESTATUSSTRING = "INCLUDESTATUSSTRING";
    public static final String INCLUDESIGNINGTIMEATTRIBUTE = "INCLUDESIGNINGTIMEATTRIBUTE";
    
    private static final String DEFAULT_WORKERLOGGER =
            DefaultTimeStampLogger.class.getName();

    private static final String DEFAULT_TIMESOURCE =
            "org.signserver.server.LocalComputerTimeSource";
    private static final int DEFAULT_MAXSERIALNUMBERLENGTH = 8;
    
    private static final String[] ACCEPTEDALGORITHMSNAMES = {
        "GOST3411",
        "MD5",
        "SHA1",
        "SHA224",
        "SHA256",
        "SHA384",
        "SHA512",
        "RIPEMD128",
        "RIPEMD160",
        "RIPEMD256"
    };
    
    private static final ASN1ObjectIdentifier[] ACCEPTEDALGORITHMSOIDS = {
        TSPAlgorithms.GOST3411,
        TSPAlgorithms.MD5,
        TSPAlgorithms.SHA1,
        TSPAlgorithms.SHA224,
        TSPAlgorithms.SHA256,
        TSPAlgorithms.SHA384,
        TSPAlgorithms.SHA512,
        TSPAlgorithms.RIPEMD128,
        TSPAlgorithms.RIPEMD160,
        TSPAlgorithms.RIPEMD256
    };

    private static final HashMap<String, ASN1ObjectIdentifier> ACCEPTEDALGORITHMSMAP =
            new HashMap<String, ASN1ObjectIdentifier>();
    private static final HashMap<ASN1ObjectIdentifier, String> ACCEPTEDALGORITHMSREVERSEMAP =
    		new HashMap<ASN1ObjectIdentifier, String>();

    static {
        for (int i = 0; i < ACCEPTEDALGORITHMSNAMES.length; i++) {
            ACCEPTEDALGORITHMSMAP.put(ACCEPTEDALGORITHMSNAMES[i],
                    ACCEPTEDALGORITHMSOIDS[i]);
            ACCEPTEDALGORITHMSREVERSEMAP.put(ACCEPTEDALGORITHMSOIDS[i],
            		ACCEPTEDALGORITHMSNAMES[i]);
        }
    }

    private static final String DEFAULT_SIGNATUREALGORITHM = "SHA1withRSA";
    private static final String DEFAULT_ORDERING = "FALSE";
    //private static final String DEFAULT_DIGESTOID   = TSPAlgorithms.SHA1;
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    private ITimeSource timeSource = null;
    private String signatureAlgorithm;
    private Set<ASN1ObjectIdentifier> acceptedAlgorithms = null;
    private Set<String> acceptedPolicies = null;
    private Set<String> acceptedExtensions = null;

    //private String defaultDigestOID = null;
    private ASN1ObjectIdentifier defaultTSAPolicyOID = null;
    
    private boolean validChain = true;

    private int maxSerialNumberLength;
    private String serialNumberError;
    
    // we restrict the allowed serial number size limit to between 64 and 160 bits
    // note: the generated serial number will always be positive
    private static final int MAX_ALLOWED_MAXSERIALNUMBERLENGTH = 20;
    private static final int MIN_ALLOWED_MAXSERIALNUMBERLENGTH = 8;
    
    private boolean includeStatusString;
    
    private String tsaName;
    private boolean tsaNameFromCert;
    private boolean includeSigningTimeAttribute;
    
    private boolean ordering;
    private boolean includeOrdering;
    
    private List<String> configErrors;
    
    @Override
    public void init(final int signerId, final WorkerConfig config,
            final WorkerContext workerContext,
            final EntityManager workerEntityManager) {
        super.init(signerId, config, workerContext, workerEntityManager);

        // Overrides the default worker logger to be this worker
        //  implementation's default instead of the WorkerSessionBean's
        if (config.getProperty("WORKERLOGGER") == null) {
            config.setProperty("WORKERLOGGER", DEFAULT_WORKERLOGGER);
        }

        // Check that the timestamp server is properly configured
        try {
            timeSource = getTimeSource();
            if (LOG.isDebugEnabled()) {
                LOG.debug("TimeStampSigner[" + signerId + "]: "
                        + "Using TimeSource: "
                        + timeSource.getClass().getName());
            }
        } catch (SignServerException e) {
            LOG.error("Could not create time source: " + e.getMessage());
        }
        
        // Get the signature algorithm
        signatureAlgorithm = config.getProperty(SIGNATUREALGORITHM, DEFAULT_SIGNATUREALGORITHM);

        /* defaultDigestOID =
            config.getProperties().getProperty(DEFAULTDIGESTOID);
        if (defaultDigestOID == null) {
            defaultDigestOID = DEFAULT_DIGESTOID;
        }*/

        final String policyId = config.getProperties().getProperty(DEFAULTTSAPOLICYOID);
        
        try {
        	if (policyId != null) {
        		defaultTSAPolicyOID = new ASN1ObjectIdentifier(policyId);
        	} else {
        		LOG.error("Error: No default TSA Policy OID have been configured");
        	}
        } catch (IllegalArgumentException iae) {
        	LOG.error("Error: TSA Policy OID " + policyId + " is invalid");
        }
       
        if (LOG.isDebugEnabled()) {
            LOG.debug("bctsp version: " + TimeStampResponseGenerator.class
                .getPackage().getImplementationVersion() + ", "
                + TimeStampRequest.class.getPackage()
                    .getImplementationVersion());
        }
        
        // Validate certificates in signer certificate chain
        final String requireValidChain = config.getProperty(REQUIREVALIDCHAIN, Boolean.FALSE.toString());
        if (Boolean.parseBoolean(requireValidChain)) {
            validChain = validateChain();
        }
        
        maxSerialNumberLength = DEFAULT_MAXSERIALNUMBERLENGTH;
        final String maxSerialNumberLengthProp = config.getProperty(MAXSERIALNUMBERLENGTH);
        
        if (maxSerialNumberLengthProp != null) {
            try {
        	maxSerialNumberLength = Integer.parseInt(maxSerialNumberLengthProp);
            } catch (NumberFormatException e) {
        	maxSerialNumberLength = -1;
                serialNumberError = "Maximum serial number length specified is invalid: \"" + maxSerialNumberLengthProp + "\"";
            }

            if (serialNumberError == null) {
                if (maxSerialNumberLength > MAX_ALLOWED_MAXSERIALNUMBERLENGTH) {
                    serialNumberError = "Maximum serial number length specified is too large: " + maxSerialNumberLength;
                } else if (maxSerialNumberLength < MIN_ALLOWED_MAXSERIALNUMBERLENGTH) {
                    serialNumberError = "Maximum serial number length specified is too small: " + maxSerialNumberLength;
                }
            }
        }
        
        includeStatusString = Boolean.parseBoolean(config.getProperty(INCLUDESTATUSSTRING, "true"));
        
        tsaName = config.getProperty(TSA);
        tsaNameFromCert = Boolean.parseBoolean(config.getProperty(TSA_FROM_CERT, "false"));
        
        if (tsaName != null && tsaNameFromCert) {
            LOG.error("Error: Can not set " + TSA_FROM_CERT + " to true and set " + TSA + " worker property at the same time");
        }
        
        includeSigningTimeAttribute = Boolean.valueOf(config.getProperty(INCLUDESIGNINGTIMEATTRIBUTE, "true"));
        
        ordering = Boolean.parseBoolean(config.getProperty(ORDERING, "false"));
        includeOrdering = Boolean.parseBoolean(config.getProperty(INCLUDEORDERING, "false"));
        
        configErrors = new LinkedList<String>();
        
        if (hasSetIncludeCertificateLevels && includeCertificateLevels == 0) {
            configErrors.add("Illegal value for property " + WorkerConfig.PROPERTY_INCLUDE_CERTIFICATE_LEVELS + ". Only numbers >= 1 supported.");
        }
    }

    /**
     * The main method performing the actual timestamp operation.
     * Expects the signRequest to be a GenericSignRequest contining a
     * TimeStampRequest
     *
     * @param signRequest
     * @param requestContext
     * @return the sign response
     * @see org.signserver.server.IProcessable#processData(org.signserver.common.ProcessRequest, org.signserver.common.RequestContext)
     */
    @Override
    public ProcessResponse processData(final ProcessRequest signRequest,
            final RequestContext requestContext) throws
                IllegalRequestException,
                CryptoTokenOfflineException,
                SignServerException {

        // Log values
        final LogMap logMap = LogMap.getInstance(requestContext);

        final ISignRequest sReq = (ISignRequest) signRequest;

        // Check that the request contains a valid TimeStampRequest object.
        if (!(signRequest instanceof GenericSignRequest)) {
            final IllegalRequestException exception =
                    new IllegalRequestException(
                    "Recieved request wasn't a expected GenericSignRequest. ");
            throw exception;
        }

        if (!((sReq.getRequestData() instanceof TimeStampRequest)
                || (sReq.getRequestData() instanceof byte[]))) {
            final IllegalRequestException exception =
                    new IllegalRequestException(
                "Recieved request data wasn't a expected TimeStampRequest. ");
            throw exception;
        }
        
        if (!validChain) {
            LOG.error("Certificate chain not correctly configured");
            throw new CryptoTokenOfflineException("Certificate chain not correctly configured");
        }

        final ITimeSource timeSrc = getTimeSource();
        if (LOG.isDebugEnabled()) {
            LOG.debug("TimeSource: " + timeSrc.getClass().getName());
        }
        final Date date = timeSrc.getGenTime();
        final BigInteger serialNumber = getSerialNumber();

        // Log values
        logMap.put(ITimeStampLogger.LOG_TSA_TIME, date == null ? null
                : String.valueOf(date.getTime()));
        logMap.put(ITimeStampLogger.LOG_TSA_SERIALNUMBER,
                serialNumber.toString(16));
        logMap.put(ITimeStampLogger.LOG_TSA_TIMESOURCE, timeSrc.getClass().getSimpleName());


        GenericSignResponse signResponse = null;
        ICryptoInstance crypto = null;
        try {
            crypto = acquireCryptoInstance(ICryptoToken.PURPOSE_SIGN, signRequest, requestContext);
            final byte[] requestbytes = (byte[]) sReq.getRequestData();

            if (requestbytes == null || requestbytes.length == 0) {
                LOG.error("Request must contain data");
                throw new IllegalRequestException("Request must contain data");
            }
            
            final TimeStampRequest timeStampRequest =
                    new TimeStampRequest(requestbytes);

            // Log values for timestamp request
            logMap.put(ITimeStampLogger.LOG_TSA_TIMESTAMPREQUEST_CERTREQ,
                    String.valueOf(timeStampRequest.getCertReq()));
            logMap.put(ITimeStampLogger.LOG_TSA_TIMESTAMPREQUEST_CRITEXTOIDS,
                    String.valueOf(timeStampRequest.getCriticalExtensionOIDs()));
            logMap.put(ITimeStampLogger.LOG_TSA_TIMESTAMPREQUEST_ENCODED,
                    new String(Base64.encode(requestbytes, false)));
            logMap.put(ITimeStampLogger.LOG_TSA_TIMESTAMPREQUEST_NONCRITEXTOIDS,
                    String.valueOf(timeStampRequest.getNonCriticalExtensionOIDs()));
            logMap.put(ITimeStampLogger.LOG_TSA_TIMESTAMPREQUEST_NOUNCE,
                    String.valueOf(timeStampRequest.getNonce()));
            logMap.put(ITimeStampLogger.LOG_TSA_TIMESTAMPREQUEST_VERSION,
                    String.valueOf(timeStampRequest.getVersion()));
            logMap.put(ITimeStampLogger
                        .LOG_TSA_TIMESTAMPREQUEST_MESSAGEIMPRINTALGOID,
                    timeStampRequest.getMessageImprintAlgOID().getId());
            logMap.put(ITimeStampLogger
                        .LOG_TSA_TIMESTAMPREQUEST_MESSAGEIMPRINTDIGEST,
                    new String(Base64.encode(
                        timeStampRequest.getMessageImprintDigest(), false)));

            final TimeStampTokenGenerator timeStampTokenGen =
                    getTimeStampTokenGenerator(crypto, timeStampRequest, logMap);

            final TimeStampResponseGenerator timeStampResponseGen =
                    getTimeStampResponseGenerator(timeStampTokenGen);

            final TimeStampResponse timeStampResponse =
                    timeStampResponseGen.generate(timeStampRequest,
                    serialNumber,
                    date, includeStatusString);

            final TimeStampToken token = timeStampResponse.getTimeStampToken();
            final byte[] signedbytes = timeStampResponse.getEncoded();
            
            // Log values for timestamp response
            if (LOG.isDebugEnabled()) {
                LOG.debug("Time stamp response status: "
                        + timeStampResponse.getStatus() + ": "
                        + timeStampResponse.getStatusString());
            }
            logMap.put(ITimeStampLogger.LOG_TSA_PKISTATUS,
                    String.valueOf(timeStampResponse.getStatus()));
            if (timeStampResponse.getFailInfo() != null) {
                logMap.put(ITimeStampLogger.LOG_TSA_PKIFAILUREINFO, 
                        String.valueOf(
                            timeStampResponse.getFailInfo().intValue()));
            }
            logMap.put(ITimeStampLogger.LOG_TSA_TIMESTAMPRESPONSE_ENCODED,
                    new String(Base64.encode(signedbytes, false)));
            logMap.put(ITimeStampLogger.LOG_TSA_PKISTATUS_STRING,
                    timeStampResponse.getStatusString());
            
            final String archiveId;
            if (token == null) {
                archiveId = serialNumber.toString(16);
            } else {
                archiveId = token.getTimeStampInfo().getSerialNumber()
                                        .toString(16);
            }
            
            final Collection<? extends Archivable> archivables = Arrays.asList(
                    new DefaultArchivable(Archivable.TYPE_REQUEST, REQUEST_CONTENT_TYPE, requestbytes, archiveId),
                    new DefaultArchivable(Archivable.TYPE_RESPONSE, RESPONSE_CONTENT_TYPE, signedbytes, archiveId)
                );

            if (signRequest instanceof GenericServletRequest) {
                signResponse = new GenericServletResponse(sReq.getRequestID(),
                        signedbytes,
                                    getSigningCertificate(signRequest, requestContext),
                                    archiveId,
                                    archivables, 
                                    RESPONSE_CONTENT_TYPE);
            } else {
                signResponse = new GenericSignResponse(sReq.getRequestID(),
                        signedbytes,
                        getSigningCertificate(signRequest, requestContext),
                        archiveId,
                        archivables);
            }

            // Put in log values
            if (date == null) {
                logMap.put(ITimeStampLogger.LOG_TSA_EXCEPTION,
                        "timeSourceNotAvailable");
            }

            // We were able to fulfill the request so the worker session bean
            // can go on and charge the client
            if (timeStampResponse.getStatus() == PKIStatus.GRANTED) {
                // The client can be charged for the request
                requestContext.setRequestFulfilledByWorker(true);
            } else {
            	logMap.put(IWorkerLogger.LOG_PROCESS_SUCCESS, String.valueOf(false));
            }

        } catch (InvalidAlgorithmParameterException e) {
            final IllegalRequestException exception =
                    new IllegalRequestException(
                    "InvalidAlgorithmParameterException: " + e.getMessage(), e);
            LOG.error("InvalidAlgorithmParameterException: ", e);
            logMap.put(ITimeStampLogger.LOG_TSA_EXCEPTION,
                    exception.getMessage());
            throw exception;
        } catch (NoSuchAlgorithmException e) {
            final IllegalRequestException exception =
                    new IllegalRequestException(
                        "NoSuchAlgorithmException: " + e.getMessage(), e);
            LOG.error("NoSuchAlgorithmException: ", e);
            logMap.put(ITimeStampLogger.LOG_TSA_EXCEPTION,
                    exception.getMessage());
            throw exception;
        } catch (NoSuchProviderException e) {
            final IllegalRequestException exception =
                    new IllegalRequestException(
                    "NoSuchProviderException: " + e.getMessage(), e);
            LOG.error("NoSuchProviderException: ", e);
            logMap.put(ITimeStampLogger.LOG_TSA_EXCEPTION,
                    exception.getMessage());
            throw exception;
        } catch (CertStoreException e) {
            final IllegalRequestException exception =
                    new IllegalRequestException("CertStoreException: "
                    + e.getMessage(), e);
            LOG.error("CertStoreException: ", e);
            logMap.put(ITimeStampLogger.LOG_TSA_EXCEPTION,
                    exception.getMessage());
            throw exception;
        } catch (IOException e) {
            final IllegalRequestException exception =
                    new IllegalRequestException(
                    "IOException: " + e.getMessage(), e);
            LOG.error("IOException: ", e);
            logMap.put(ITimeStampLogger.LOG_TSA_EXCEPTION,
                    exception.getMessage());
            throw exception;
        } catch (TSPException e) {
            final IllegalRequestException exception =
                    new IllegalRequestException(e.getMessage(), e);
            LOG.error("TSPException: ", e);
            logMap.put(ITimeStampLogger.LOG_TSA_EXCEPTION,
                    exception.getMessage());
            throw exception;
        } catch (OperatorCreationException e) {
        	final SignServerException exception =
        			new SignServerException(e.getMessage(), e);
        	LOG.error("OperatorCreationException: ", e);
        	logMap.put(ITimeStampLogger.LOG_TSA_EXCEPTION,
        			exception.getMessage());
        	throw exception;
        } finally {
            releaseCryptoInstance(crypto, requestContext);
        }

        return signResponse;
    }

    /**
     * @return a time source interface expected to provide accurate time
     */
    private ITimeSource getTimeSource() throws SignServerException {
        if (timeSource == null) {
            try {
                String classpath =
                        this.config.getProperties().getProperty(TIMESOURCE);
                if (classpath == null) {
                    classpath = DEFAULT_TIMESOURCE;
                }

                final Class<?> implClass = Class.forName(classpath);
                final Object obj = implClass.newInstance();
                timeSource = (ITimeSource) obj;
                timeSource.init(config.getProperties());

            } catch (ClassNotFoundException e) {
                throw new SignServerException("Class not found", e);
            } catch (IllegalAccessException iae) {
                throw new SignServerException("Illegal access", iae);
            } catch (InstantiationException ie) {
                throw new SignServerException("Instantiation error", ie);
            }
        }

        return timeSource;
    }

    @SuppressWarnings("unchecked")
    private Set<ASN1ObjectIdentifier> getAcceptedAlgorithms() {
        if (acceptedAlgorithms == null) {
            final String nonParsedAcceptedAlgorihms =
                    this.config.getProperties().getProperty(ACCEPTEDALGORITHMS);
            if (nonParsedAcceptedAlgorihms == null) {
                acceptedAlgorithms = TSPAlgorithms.ALLOWED;
            } else {
                final String[] subStrings =
                        nonParsedAcceptedAlgorihms.split(";");
                if (subStrings.length > 0) {
                    acceptedAlgorithms = new HashSet();
                    for (int i = 0; i < subStrings.length; i++) {
                        final ASN1ObjectIdentifier acceptAlg = ACCEPTEDALGORITHMSMAP.get(subStrings[i]);
                        if (acceptAlg != null) {
                            acceptedAlgorithms.add(acceptAlg);
                        } else {
                            LOG.error("Error, signer " + workerId
                                    + " configured with incompatible acceptable algorithm : "
                                    + subStrings[i]);
                        }
                    }
                }
            }
        }

        return acceptedAlgorithms;
    }

    private Set<String> getAcceptedPolicies() {
        if (acceptedPolicies == null) {
            final String nonParsedAcceptedPolicies =
                    this.config.getProperties().getProperty(ACCEPTEDPOLICIES);
            acceptedPolicies = makeSetOfProperty(nonParsedAcceptedPolicies);
        }

        return acceptedPolicies;

    }

    private Set<String> getAcceptedExtensions() {
        if (acceptedExtensions == null) {
            final String nonParsedAcceptedExtensions =
                    this.config.getProperties().getProperty(ACCEPTEDEXTENSIONS);
            acceptedExtensions = makeSetOfProperty(nonParsedAcceptedExtensions);
        }

        return acceptedExtensions;
    }

    /**
     * Help method taking a string and creating a java.util.Set of the
     * strings using ';' as a delimiter.
     * If null is used as and argument then will null be returned by the method.
     * @param nonParsedPropery Semicolon separated strings
     * @return Set of Strings
     */
    private Set<String> makeSetOfProperty(final String nonParsedPropery) {
        Set<String> retval = new HashSet<String>();
        if (nonParsedPropery != null) {
            final String[] subStrings = nonParsedPropery.split(";");
            for (String oid : subStrings) {
                oid = oid.trim();
                if (!oid.isEmpty()) {
                    retval.add(oid);
                }
            }
        }
        return retval;
    }

    private TimeStampTokenGenerator getTimeStampTokenGenerator(
            final ICryptoInstance crypto,
            final TimeStampRequest timeStampRequest,
            final LogMap logMap)
            throws
                IllegalRequestException,
                CryptoTokenOfflineException,
                InvalidAlgorithmParameterException,
                NoSuchAlgorithmException,
                NoSuchProviderException,
                CertStoreException,
                OperatorCreationException,
                SignServerException {

        TimeStampTokenGenerator timeStampTokenGen = null;
        try {
            ASN1ObjectIdentifier tSAPolicyOID = timeStampRequest.getReqPolicy();
            if (tSAPolicyOID == null) {
                tSAPolicyOID = defaultTSAPolicyOID;
            }
            logMap.put(ITimeStampLogger.LOG_TSA_POLICYID, tSAPolicyOID.getId());

            final X509Certificate signingCert
                    = (X509Certificate) getSigningCertificate(crypto);
            if (signingCert == null) {
                throw new CryptoTokenOfflineException(
                        "No certificate for this signer");
            }
            
            DigestCalculatorProvider calcProv = new BcDigestCalculatorProvider();    
            DigestCalculator calc = calcProv.get(new AlgorithmIdentifier(TSPAlgorithms.SHA1));

            ContentSigner cs =
            		new JcaContentSignerBuilder(signatureAlgorithm).setProvider(crypto.getProvider()).build(crypto.getPrivateKey());
            JcaSignerInfoGeneratorBuilder sigb = new JcaSignerInfoGeneratorBuilder(calcProv);
            X509CertificateHolder certHolder = new X509CertificateHolder(signingCert.getEncoded());
            
            // set signed attribute table generator based on property
            sigb.setSignedAttributeGenerator(
                    new OptionalSigningTimeSignedAttributeTableGenerator(includeSigningTimeAttribute));
            
            SignerInfoGenerator sig = sigb.build(cs, certHolder);
            
            timeStampTokenGen = new TimeStampTokenGenerator(calc, sig, tSAPolicyOID);

            if (config.getProperties().getProperty(ACCURACYMICROS) != null) {
                timeStampTokenGen.setAccuracyMicros(Integer.parseInt(
                        config.getProperties().getProperty(ACCURACYMICROS)));
            }

            if (config.getProperties().getProperty(ACCURACYMILLIS) != null) {
                timeStampTokenGen.setAccuracyMillis(Integer.parseInt(
                        config.getProperties().getProperty(ACCURACYMILLIS)));
            }

            if (config.getProperties().getProperty(ACCURACYSECONDS) != null) {
                timeStampTokenGen.setAccuracySeconds(Integer.parseInt(
                        config.getProperties().getProperty(ACCURACYSECONDS)));
            }

            timeStampTokenGen.setOrdering(ordering);
            timeStampTokenGen.setIncludeOrdering(includeOrdering);

            if (tsaName != null) {
                final X500Name x500Name = new X500Name(tsaName);
                timeStampTokenGen.setTSA(new GeneralName(x500Name));
            } else if (tsaNameFromCert) {
                final X500Name x500Name = new JcaX509CertificateHolder(signingCert).getSubject();
                timeStampTokenGen.setTSA(new GeneralName(x500Name));
            }
           
            timeStampTokenGen.addCertificates(getCertStoreWithChain(signingCert, getSigningCertificateChain(crypto)));

        } catch (IllegalArgumentException e) {
            LOG.error("IllegalArgumentException: ", e);
            throw new IllegalRequestException(e.getMessage());
        } catch (TSPException e) {
            LOG.error("TSPException: ", e);
            throw new IllegalRequestException(e.getMessage());
        } catch (CertificateEncodingException e) {
        	LOG.error("CertificateEncodingException: ", e);
        	throw new IllegalRequestException(e.getMessage());
        } catch (IOException e) {
        	LOG.error("IOException: ", e);
        	throw new IllegalRequestException(e.getMessage());
        }

        return timeStampTokenGen;
    }

    private TimeStampResponseGenerator getTimeStampResponseGenerator(
            TimeStampTokenGenerator timeStampTokenGen) {
        
        return new TimeStampResponseGenerator(timeStampTokenGen,
                this.getAcceptedAlgorithms(),
                this.getAcceptedPolicies(),
                this.getAcceptedExtensions());
    }

    /**
     * Help method that generates a serial number using SecureRandom.
     * Uses the configured length of the signer. This is public to allow using directly from
     * unit test.
     */
    public BigInteger getSerialNumber() throws SignServerException {
        BigInteger serialNumber = null;
        
        if (maxSerialNumberLength < MIN_ALLOWED_MAXSERIALNUMBERLENGTH
        	|| maxSerialNumberLength > MAX_ALLOWED_MAXSERIALNUMBERLENGTH) {
        	throw new SignServerException("Maximum serial number length is not in allowed range");
        }
        
        try {
            serialNumber = getSerno(maxSerialNumberLength);
        } catch (Exception e) {
            LOG.error("Error initiating Serial Number generator, SEVERE ERROR.",
                    e);
        }
        return serialNumber;
    }

    /**
     * Generates a number of serial number bytes. The number returned should
     * be a positive number.
     *
     * @param maxLength the maximum number of octects of the generated serial number
     * @return a BigInteger with a new random serial number.
     */
    public BigInteger getSerno(int maxLength) {
        if (random == null) {
            try {
                random = SecureRandom.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                LOG.error(e);
            }
        }
        
        final byte[] sernobytes = new byte[maxLength];
        random.nextBytes(sernobytes);
        BigInteger serno = new BigInteger(sernobytes).abs();
       
        return serno;
    }
    
    private static class SHA1DigestCalculator implements DigestCalculator {
    	private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    	private MessageDigest digest;

    	public SHA1DigestCalculator() {
    		try {
    			this.digest = MessageDigest.getInstance("SHA1");
    		} catch (NoSuchAlgorithmException e) {
    			
    		}
    	}

    	public AlgorithmIdentifier getAlgorithmIdentifier() {
    		return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
    	}

    	public OutputStream getOutputStream() {
    		return bOut;
    	}

    	public byte[] getDigest() {
    		byte[] bytes = digest.digest(bOut.toByteArray());

    		bOut.reset();

    		return bytes;
    	}
    }

    /**
     * @return True if each certificate in the certificate chain can be verified 
     * by the next certificate (if any). This does not check that the last 
     * certificate is a trusted certificate as the root certificate is normally 
     * not included.
     */
    private boolean validateChain() {
        boolean result = true;
        try {
            final List<Certificate> signingCertificateChain =
                    getSigningCertificateChain();
            if (signingCertificateChain != null) {
                List<Certificate> chain = (List<Certificate>) signingCertificateChain;
                for (int i = 0; i < chain.size(); i++) {
                    Certificate subject = chain.get(i);
                    
                    // If we have the issuer we can validate the certificate
                    if (chain.size() > i + 1) {
                        Certificate issuer = chain.get(i + 1);
                        try {
                            subject.verify(issuer.getPublicKey(), "BC");
                        } catch (CertificateException ex) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Certificate could not be verified: " + ex.getMessage() + ": " + subject);
                            }
                            result = false;
                        } catch (NoSuchAlgorithmException ex) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Certificate could not be verified: " + ex.getMessage() + ": " + subject);
                            }
                            result = false;
                        } catch (InvalidKeyException ex) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Certificate could not be verified: " + ex.getMessage() + ": " + subject);
                            }
                            result = false;
                        } catch (NoSuchProviderException ex) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Certificate could not be verified: " + ex.getMessage() + ": " + subject);
                            }
                            result = false;
                        } catch (SignatureException ex) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Certificate could not be verified: " + ex.getMessage() + ": " + subject);
                            }
                            result = false;
                        }
                    }
                }
            } else {
                // This would be a bug
                LOG.error("Certificate chain was not an list!");
                result = false;
            }
        } catch (CryptoTokenOfflineException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to get signer certificate or chain: " + ex.getMessage());
            }
            result = false;
        }
        return result;
    }

    @Override
    protected List<String> getFatalErrors() {
        final List<String> result = new LinkedList<String>();
        result.addAll(super.getFatalErrors());
        result.addAll(configErrors);
        
        try {
            // Check signer certificate chain if required
            if (!validChain) {
                result.add("Not strictly valid chain and " + REQUIREVALIDCHAIN + " specified");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Signer " + workerId + ": " + REQUIREVALIDCHAIN + " specified but the chain was not found valid");
                }
            }

            // Check if certificat has the required EKU
            final Certificate certificate = getSigningCertificate();
            try {
                if (certificate instanceof X509Certificate) {
                    final X509Certificate cert = (X509Certificate) certificate;
                    final List<String> ekus = cert.getExtendedKeyUsage();
                    
                    if (ekus == null 
                            || !ekus.contains(KeyPurposeId.id_kp_timeStamping.getId())) {
                        result.add("Missing extended key usage timeStamping");
                    }
                    if (cert.getCriticalExtensionOIDs() == null 
                            || !cert.getCriticalExtensionOIDs().contains(org.bouncycastle.asn1.x509.X509Extension.extendedKeyUsage.getId())) {
                        result.add("The extended key usage extension must be present and marked as critical");
                    }
                    // if extended key usage contains timeStamping and also other
                    // usages
                    if (ekus != null
                            && ekus.contains(KeyPurposeId.id_kp_timeStamping.getId())
                            && ekus.size() > 1) {
                        result.add("No other extended key usages than timeStamping is allowed");
                    }
                } else {
                    result.add("Unsupported certificate type");
                }
            } catch (CertificateParsingException ex) {
                result.add("Unable to parse certificate");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Signer " + workerId + ": Unable to parse certificate: " + ex.getMessage());
                }
            }
        } catch (CryptoTokenOfflineException ex) {
            result.add("No signer certificate available");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Signer " + workerId + ": Could not get signer certificate: " + ex.getMessage());
            }
        } 
        
        // check time source
        if (timeSource.getGenTime() == null) {
        	result.add("Time source not available");
        	if (LOG.isDebugEnabled()) {
        		LOG.debug("Signer " + workerId + ": time source not available");
        	}
        }

        final String serialNumberError = getSerialNumberError();

        if (serialNumberError != null) {
            result.add(serialNumberError);
        }
        
        // check default policy
        if (defaultTSAPolicyOID == null) {
            result.add("No default TSA policy OID has been configured, or is invalid");
        }

        // check TSA naming properties conflict
        if (tsaName != null && tsaNameFromCert) {
            result.add("Can not set " + TSA_FROM_CERT + " to true and set " + TSA + " worker property at the same time");
        }

        if (ordering && !includeOrdering) {
            result.add("INCLUDEORDERING can not be set to \"false\" when ORDERING is set to \"true\"");
        }
        
        return result;
    }
    
    /**
     * Get serial number error
     * We run this stand-alone from the unit test
     * 
     */
    protected String getSerialNumberError() {
        return serialNumberError;
    }
    
    
}
