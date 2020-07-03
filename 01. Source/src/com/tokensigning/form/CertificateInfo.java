package com.tokensigning.form;

public class CertificateInfo {
	public CertificateInfo(String subjectCN, String serial, String base64,
			String issuerCN, String notBefore, String notAfter) {
		super();
		this.subjectCN = subjectCN;
		this.serial = serial;
		this.base64 = base64;
		this.issuerCN = issuerCN;
		this.notBefore = notBefore;
		this.notAfter = notAfter;
	}
	private String subjectCN;
	private String serial;
	private String base64;
	private String issuerCN;
	private String notBefore;
	private String notAfter;
	private String subjectOU;
	private String subjectSN;
	
	public CertificateInfo(String subjectCN, String serialNumber) {
		super();
		this.subjectCN = subjectCN;
		this.serial = serialNumber;
	}
	
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getBase64() {
		return base64;
	}
	public void setBase64(String base64) {
		this.base64 = base64;
	}
	public String getIssuerCN() {
		return issuerCN;
	}
	public void setIssuerCN(String issuerCN) {
		this.issuerCN = issuerCN;
	}
	public String getNotBefore() {
		return notBefore;
	}
	public void setNotBefore(String notBefore) {
		this.notBefore = notBefore;
	}
	public String getNotAfter() {
		return notAfter;
	}
	public void setNotAfter(String notAfter) {
		this.notAfter = notAfter;
	}
	
	public String getSubjectCN() {
		return subjectCN;
	}
	public void setSubjectCN(String subjectCN) {
		this.subjectCN = subjectCN;
	}

	public String getSubjectSN() {
		return subjectSN;
	}

	public void setSubjectSN(String subjectSN) {
		this.subjectSN = subjectSN;
	}

	public String getSubjectOU() {
		return subjectOU;
	}

	public void setSubjectOU(String subjectOU) {
		this.subjectOU = subjectOU;
	}
	
}
