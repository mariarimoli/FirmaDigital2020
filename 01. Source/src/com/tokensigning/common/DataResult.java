package com.tokensigning.common;

/**
* DataResult: object define response's data from TokenSigning to Webclient
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class DataResult
{
	// Error code
	private int code;
	// Data: data sgned or certificate json string....
	private String data;
	// Error: error description
	private String error;
	
	public DataResult() {
	}
	
	public DataResult(int code, String data) {
		this.code = code;
		this.data = data;
	}
	
	public DataResult(int code, String data, String error) {
		this.code = code;
		this.data = data;
		this.error = error;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getBaseData() {
		return data;
	}
	public void setBaseData(String baseData) {
		this.data = baseData;
	}
	
	public String getErrDesc() {
		return error;
	}
	public void setErrDesc(String errDesc) {
		this.error = errDesc;
	}
	
}