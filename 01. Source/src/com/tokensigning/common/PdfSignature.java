package com.tokensigning.common;

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

	private int page;
	private String rectangle;
}
