package com.tokensigning.common;


/**
* Comment: object define file selected
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class FileInfo {
	public FileInfo(String path, String base64) {
		super();
		this.path = path;
		this.base64 = base64;
	}
	// File path
	private String path;
	// File content encoded base64
		private String base64;
	
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
	
}
