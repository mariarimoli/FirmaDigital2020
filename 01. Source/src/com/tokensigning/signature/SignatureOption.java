package com.tokensigning.signature;

import com.tokensigning.form.CertificateInfo;

/**
* SignatureOption: signature option base
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class SignatureOption {
	// signature hash algorithm
	public String digestMethod;
	
	// signature time. format:  HH:mm:ss dd/MM/yyyy
	public String SigningTime;
	
	// certificate serial to sign
	public String certificateSerial;
	
	// certificate store
	private int certStore = CertificateInfo.USER_STORE;
	
	public SignatureOption() {
		this.digestMethod = "SHA1";
		this.signatureMethod = "SHA1withRSA";
	}
	public SignatureOption(String digestMethod, String signatureMethod) {
		super();
		this.digestMethod = digestMethod;
		this.signatureMethod = signatureMethod;
	}
	public String getCertificateSerial() {
		return certificateSerial;
	}
	public void setCertificateSerial(String certificateSerial) {
		this.certificateSerial = certificateSerial;
	}
	public String signatureMethod;
	// method
    public String getSigningTime() {
		return SigningTime;
	}
	public void setSigningTime(String signingTime) {
		SigningTime = signingTime;
	}
	public String getDigestMethod() {
		return digestMethod;
	}
	public void setDigestMethod(String digestMethod) {
		this.digestMethod = digestMethod;
	}
	public String getSignatureMethod() {
		return signatureMethod;
	}
	public void setSignatureMethod(String signatureMethod) {
		this.signatureMethod = signatureMethod;
	}
	public int getCertStore() {
		return certStore;
	}
	public void setCertStore(int certStore) {
		this.certStore = certStore;
	}	
}
