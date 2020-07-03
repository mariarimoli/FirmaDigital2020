package com.tokensigning.common;

public class TSResponse
{
    private int code;
    private String data;
    private String error;
    
    
    public TSResponse() {
    	
    }
    
    public TSResponse(int code, String data, String error) {
		this.code = code;
		this.data = data;
		this.error = error;
	}
    
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
}