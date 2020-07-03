package com.tokensigning.signature;

public class XmlSignatureOption extends SignatureOption {
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
	public String ParrentTagSigning;
    public String TagSigning;
    public String NodeToSign;
    public String TagSaveResult;
    public String NameXPathFilter;
    public String NameIDTimeSignature;
    public Boolean DsSignature;
    public String SigningType;
    public String CertificateSerial;
    public Boolean ValidateBefore;
}
