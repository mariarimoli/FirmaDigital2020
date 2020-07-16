package com.tokensigning.common;

/**
* CertificateFilter: object define conditions to filter certificates when get certificates 
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class CertificateFilter {
	
	private String serialNumber;
	private int    certStore;
	
	/**
     * Returns the serial filter
     *
     * @param 
     * @return serial number 
     */
	public String getSerialNumber() {
		return serialNumber;
	}
	
	/**
     * Set serial to filter
     *
     * @param serialNumber
     * @return 
     */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	/**
     * Returns certificate store type
     *
     * @param 
     * @return certStore
     */
	public int getCertStore() {
		return certStore;
	}
	

	/**
     * Set certificate store type - 0: All, 1: User, 2: Local
     *
     * @param certStore
     * @return
     */
	public void setCertStore(int certStore) {
		this.certStore = certStore;
	}
}