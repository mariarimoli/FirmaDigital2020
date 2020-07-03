package com.tokensigning.common;

public class DataResult
{
	private int code;
	private String data;
	private String error;
	
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