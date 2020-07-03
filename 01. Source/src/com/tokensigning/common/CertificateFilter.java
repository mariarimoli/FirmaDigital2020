package com.tokensigning.common;

public class CertificateFilter {
	private String serialNumber;
	private int certStore;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getCertStore() {
		return certStore;
	}
	public void setCertStore(int certStore) {
		this.certStore = certStore;
	}
}
