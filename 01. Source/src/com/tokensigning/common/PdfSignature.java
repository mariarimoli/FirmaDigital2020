package com.tokensigning.common;

/**
* PdfSignature: define signature on pdf file
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/
public class PdfSignature {
	public String getRectangle() {
		return rectangle;
	}
	public void setRectangle(String rectangle) {
		this.rectangle = rectangle;
	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}

	// page
	private int page;
	// signature rectangle
	private String rectangle;
}
