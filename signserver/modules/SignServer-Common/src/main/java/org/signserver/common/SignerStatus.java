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
package org.signserver.common;

import java.io.PrintStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.ejbca.util.CertTools;

/**
 * Class used when responding to the SignSession.getStatus() method, represents
 * the status of a specific signer.
 *
 * FIXME: This feature should be re-designed. See DSS-304.
 *
 * @author Philip Vendil
 * @version $Id$
 */
public class SignerStatus extends CryptoTokenStatus {
	
    private static final long serialVersionUID = 2L;

    private transient Certificate signerCertificate;
    private byte[] signerCertificateBytes;

    private long keyUsageCounterValue;
    private boolean keyUsageCounterDisabled;
    
	/** 
	 * @deprecated Use a constructor that takes an list of errors instead.
	 */
    @Deprecated
	public SignerStatus(int workerId, int tokenStatus, ProcessableConfig config, Certificate signerCertificate){
        this(workerId, tokenStatus, Collections.<String>emptyList(), config, signerCertificate);
	}

    /** 
	 * @deprecated Use a constructor that takes an list of errors instead.
	 */
    @Deprecated
    public SignerStatus(final int workerId, final int status,
            final ProcessableConfig config, final Certificate signerCertificate,
            final long counter) {
        this(workerId, status, Collections.<String>emptyList(), config, signerCertificate, counter);
    }
    
    public SignerStatus(int workerId, int tokenStatus, List<String> errors, ProcessableConfig config, Certificate signerCertificate){
            super(workerId, tokenStatus, errors, config.getWorkerConfig());
            this.signerCertificate = signerCertificate;
            try {
                this.signerCertificateBytes = signerCertificate == null ? null 
                        : signerCertificate.getEncoded();
            } catch (CertificateEncodingException ex) {
                throw new RuntimeException(ex);
            }
	}

    public SignerStatus(final int workerId, final int status, List<String> errors,
            final ProcessableConfig config, final Certificate signerCertificate,
            final long counter) {
        this(workerId, status, errors, config, signerCertificate);
        this.keyUsageCounterValue = counter;
    }
    
    /**
     * Method used to retrieve the currently used signercertficate.
     * Use this method when checking status and not from config, since the cert isn't always in db.
     */
    public Certificate getSignerCertificate() {
        if (signerCertificate == null && signerCertificateBytes != null) {
            try {
                signerCertificate = CertTools.getCertfromByteArray(signerCertificateBytes);
            } catch (CertificateException ex) {
                throw new RuntimeException(ex);
            }
        }
        return signerCertificate;
    }

	@Override
	public void displayStatus(int workerId, PrintStream out, boolean complete) {
        final List<String> errors = getFatalErrors();
		out.println("Status of Signer with Id " + workerId + " is :\n"  
                + "  Worker status : " + signTokenStatuses[getTokenStatus() == CryptoTokenStatus.STATUS_ACTIVE && (errors.isEmpty()) ? 1 : 2] + "\n"
                + "  Token status  : " + signTokenStatuses[getTokenStatus()]);
        
        out.print("  Signings: " + keyUsageCounterValue);
        long keyUsageLimit = -1;
        try {
            keyUsageLimit = Long.valueOf(getActiveSignerConfig()
                .getProperty(SignServerConstants.KEYUSAGELIMIT));
        } catch(NumberFormatException ignored) {}
        if (keyUsageLimit >= 0) {
            out.print(" of " + keyUsageLimit);
        }
        if (isKeyUsageCounterDisabled()) {
            out.print(" (counter disabled)");
        }
        out.println();
        
        if (errors != null && !errors.isEmpty()) {
            out.println("  Errors: ");
            
            for (String error : errors) {
                out.print("    ");
                out.println(error);
            }
        }
        if (isDisabled()) {
            out.println("   Signer is disabled.");
        }       

                out.println("\n\n");

		if(complete){
			out.println("Active Properties are :");


			if(getActiveSignerConfig().getProperties().size() == 0){
				out.println("  No properties exists in active configuration\n");
			}

			Enumeration<?> propertyKeys = getActiveSignerConfig().getProperties().keys();
			while(propertyKeys.hasMoreElements()){
				String key = (String) propertyKeys.nextElement();
				out.println("  " + key + "=" + getActiveSignerConfig().getProperties().getProperty(key) + "\n");
			}        		

			out.println("\n");

			out.println("Active Authorized Clients are are (Cert DN, IssuerDN):");
			Iterator<?> iter =  new ProcessableConfig(getActiveSignerConfig()).getAuthorizedClients().iterator();
			while(iter.hasNext()){
				AuthorizedClient client = (AuthorizedClient) iter.next();
				out.println("  " + client.getCertSN() + ", " + client.getIssuerDN() + "\n");
			}
			if(getSignerCertificate() == null){
				out.println("Error: No Signer Certificate have been uploaded to this signer.\n");	
			}else{
				out.println("The current configuration use the following signer certificate : \n");
				printCert((X509Certificate) getSignerCertificate(),out );
			}
		}		
	}

    public boolean isKeyUsageCounterDisabled() {
        return keyUsageCounterDisabled;
    }

    public void setKeyUsageCounterDisabled(boolean keyUsageCounterDisabled) {
        this.keyUsageCounterDisabled = keyUsageCounterDisabled;
    }
		
}