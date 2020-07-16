package com.tokensigning.license;

/**
* LicenseContent: object define license key information
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class LicenseContent
{
    private String domain;
    private String notAfer;
    private String notBefore;
    private String os;
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getNotAfer() {
		return notAfer;
	}
	public void setNotAfer(String notAfer) {
		this.notAfer = notAfer;
	}
	public String getNotBefore() {
		return notBefore;
	}
	public void setNotBefore(String notBefore) {
		this.notBefore = notBefore;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
}
