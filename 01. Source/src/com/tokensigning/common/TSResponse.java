package com.tokensigning.common;

/**
* DataResult: object define response from TokenSigning to Webclient
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class TSResponse
{
	// Error code
    private int code;
    // Data: json string 	
    private String data;
    // Error: error description
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