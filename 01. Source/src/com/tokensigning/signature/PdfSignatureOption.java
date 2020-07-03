package com.tokensigning.signature;

import java.util.HashMap;
import java.util.List;

import com.lowagie.text.pdf.PdfSignatureAppearance.SignatureComment;
import com.tokensigning.common.PdfSignature;

public class PdfSignatureOption extends SignatureOption{
	
	
	public int page = 1; 
    public int llx = 10;
    public int lly = 10;
    public int urx = 150;
    public int ury = 75;
    
    
    private int SigType;
    private String Signer ;
    //public String SigningTime ;// HH:mm:ss dd/MM/yyyy
    
    private String ImageBase64 ;
    private Boolean ValidationOption;
    
    private String SigColorRGB;
    private String fontColor;
    private int SigTextSize;
    	
    private String Location;
    private String Reason;
    private String DigestAlgrothim;
    private String tsaUrl;
    private String tsaUserName;
    private String tsaPass;
    private String signatureText;
    private List<PdfSignature> signatures;
    private SignatureComment[] comments;
    private HashMap<String, String> metadatas;
    
    public PdfSignatureOption(int page, int llx, int lly, int urx, int ury, String ImageBase64, Boolean ValidationOption, 
    		String SigColorRGB, int SigTextSize, String DigestAlgrothim)
    {
    	// page
    	this.page = page;
    	// toa do
    	this.llx = llx;
    	this.lly = lly;
    	this.urx = urx;
    	this.ury = ury;
    	// anh
    	this.ImageBase64 = ImageBase64;
    	// bieu tuong validate
    	this.ValidationOption = ValidationOption;
    	// mau chu
    	this.SigColorRGB = SigColorRGB;
    	// kich thuoc
    	this.SigTextSize = SigTextSize;
    	// thuat toan
    	this.DigestAlgrothim = DigestAlgrothim;    	
    }
	
	
    public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getLlx() {
		return llx;
	}
	public void setLlx(int llx) {
		this.llx = llx;
	}
	public int getLly() {
		return lly;
	}
	public void setLly(int lly) {
		this.lly = lly;
	}
	public int getUrx() {
		return urx;
	}
	public void setUrx(int urx) {
		this.urx = urx;
	}
	public int getUry() {
		return ury;
	}
	public void setUry(int ury) {
		this.ury = ury;
	}
	public String getSigner() {
		return Signer;
	}
	public void setSigner(String signer) {
		Signer = signer;
	}
	
	public String getImageBase64() {
		return ImageBase64;
	}
	public void setImageBase64(String imageBase64) {
		ImageBase64 = imageBase64;
	}
	
	public Boolean getValidationOption() {
		return ValidationOption;
	}
	public void setValidationOption(Boolean validationOption) {
		ValidationOption = validationOption;
	}
	
	public String getSigColorRGB() {
		return SigColorRGB;
	}
	public void setSigColorRGB(String sigColorRGB) {
		SigColorRGB = sigColorRGB;
	}
	public int getSigTextSize() {
		return SigTextSize;
	}
	public void setSigTextSize(int sigTextSize) {
		SigTextSize = sigTextSize;
	}
	
	public String getReason() {
		return Reason;
	}
	public void setReason(String reason) {
		Reason = reason;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getDigestAlgrothim() {
		return DigestAlgrothim;
	}
	public void setDigestAlgrothim(String digestAlgrothim) {
		DigestAlgrothim = digestAlgrothim;
	}
	
	/**
	 * @return the sigType
	 */
	public int getSigType() {
		return SigType;
	}
	/**
	 * @param sigType the sigType to set
	 */
	public void setSigType(int sigType) {
		SigType = sigType;
	}


	public SignatureComment[] getComments() {
		return comments;
	}


	public void setComments(SignatureComment[] comments) {
		this.comments = comments;
	}


	public List<PdfSignature> getSignatures() {
		return signatures;
	}


	public void setSignatures(List<PdfSignature> signatures) {
		this.signatures = signatures;
	}


	public String getFontColor() {
		return fontColor;
	}


	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}


	public String getTsaUrl() {
		return tsaUrl;
	}


	public void setTsaUrl(String tsaUrl) {
		this.tsaUrl = tsaUrl;
	}


	public String getTsaUserName() {
		return tsaUserName;
	}


	public void setTsaUserName(String tsaUserName) {
		this.tsaUserName = tsaUserName;
	}


	public String getTsaPass() {
		return tsaPass;
	}


	public void setTsaPass(String tsaPass) {
		this.tsaPass = tsaPass;
	}


	public String getSignatureText() {
		return signatureText;
	}


	public void setSignatureText(String signatureText) {
		this.signatureText = signatureText;
	}


	public HashMap<String, String> getMetadatas() {
		return metadatas;
	}


	public void setMetadatas(HashMap<String, String> metadatas) {
		this.metadatas = metadatas;
	}

}