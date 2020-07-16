package com.tokensigning.signature;

/**
* XmlSignatureOption: define xml signature options
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class XmlSignatureOption extends SignatureOption {
	//
	public String ParrentTagSigning;
	// xml tag signing
    public String TagSigning;
    // id of tag signing
    public String NodeToSign;
    // xml tag save signature
    public String TagSaveResult;
    // xml tag xpath filter
    public String NameXPathFilter;
    // id of tag signing time
    public String NameIDTimeSignature;
    // use ds prefix or not
    public Boolean DsSignature;
    // signature type: enveloped, enveloping, detached
    public String SigningType;
    // certificate to sign 
    public String CertificateSerial;
    // Verify data befor sign
    public Boolean ValidateBefore;
    
    public String getParrentTagSigning() {
		return ParrentTagSigning;
	}
	public void setParrentTagSigning(String parrentTagSigning) {
		ParrentTagSigning = parrentTagSigning;
	}
	public String getTagSigning() {
		return TagSigning;
	}
	public void setTagSigning(String tagSigning) {
		TagSigning = tagSigning;
	}
	public String getNodeToSign() {
		return NodeToSign;
	}
	public void setNodeToSign(String nodeToSign) {
		NodeToSign = nodeToSign;
	}
	public String getTagSaveResult() {
		return TagSaveResult;
	}
	public void setTagSaveResult(String tagSaveResult) {
		TagSaveResult = tagSaveResult;
	}
	public String getNameXPathFilter() {
		return NameXPathFilter;
	}
	public void setNameXPathFilter(String nameXPathFilter) {
		NameXPathFilter = nameXPathFilter;
	}
	public String getNameIDTimeSignature() {
		return NameIDTimeSignature;
	}
	public void setNameIDTimeSignature(String nameIDTimeSignature) {
		NameIDTimeSignature = nameIDTimeSignature;
	}
	public Boolean getDsSignature() {
		return DsSignature;
	}
	public void setDsSignature(Boolean dsSignature) {
		DsSignature = dsSignature;
	}
	public String getSigningType() {
		return SigningType;
	}
	public void setSigningType(String signingType) {
		SigningType = signingType;
	}
	public String getCertificateSerial() {
		return CertificateSerial;
	}
	public void setCertificateSerial(String certificateSerial) {
		CertificateSerial = certificateSerial;
	}
	public Boolean getValidateBefore() {
		return ValidateBefore;
	}
	public void setValidateBefore(Boolean validateBefore) {
		ValidateBefore = validateBefore;
	}
	
	
}
