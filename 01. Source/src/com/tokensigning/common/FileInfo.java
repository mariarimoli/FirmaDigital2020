package com.tokensigning.common;

public class FileInfo {
	public FileInfo(String path, String base64) {
		super();
		this.path = path;
		this.base64 = base64;
	}
	private String path;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getBase64() {
		return base64;
	}
	public void setBase64(String base64) {
		this.base64 = base64;
	}
	private String base64;
}
