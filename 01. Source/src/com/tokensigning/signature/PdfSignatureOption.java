package com.tokensigning.signature;

import java.util.HashMap;
import java.util.List;

import com.lowagie.text.pdf.PdfSignatureAppearance.SignatureComment;
import com.tokensigning.common.PdfSignature;

/**
* PdfSignatureOption: define pdf signature options
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/
public class PdfSignatureOption extends SignatureOption{
	
	// signature page
	public int page = 1; 
	// signature location in pdf file
    public int llx = 10;
    public int lly = 10;
    public int urx = 150;
    public int ury = 75;
    
    // signature type is VisibleType (class VisibleType)
    private int SigType;
    
    // signature signer
    private String Signer ;
    
    // signature logo (image)
    private String ImageBase64 ;
    
    // signature validation onption (support on pdf foxit, adobe)
    private Boolean ValidationOption;
    
    // signature color (code rgb)
    private String SigColorRGB;
    
    // signature text color
    private String fontColor;
    
    // signature text size
    private int SigTextSize;
    
    // signature location
    private String Location;
    
    // signature reason
    private String Reason;
    
    // signature hash algorithm
    private String DigestAlgrothim;
    
    // signature timestamp
    private String tsaUrl;
    private String tsaUserName;
    private String tsaPass;
    
    // text in signature
    private String signatureText;
    
    // signatures list in pdf file
    private List<PdfSignature> signatures;
    
    // comments list in pdf file
    private SignatureComment[] comments;
    
    // properties list in pdf file
    private HashMap<String, String> metadatas;
    
    // contructor
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