package com.tokensigning.signature;

public class SignatureOption {
	public String digestMethod;
	public String SigningTime ;// HH:mm:ss dd/MM/yyyy
	public String certificateSerial;
	
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
}
