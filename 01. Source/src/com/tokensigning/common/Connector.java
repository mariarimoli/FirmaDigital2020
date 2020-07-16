package com.tokensigning.common;

/**
* Connector: object define request from webclient to TokenSigning
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class Connector {
	public Connector(int functionID, String[] args) {
		this.functionID = functionID;
		this.args = args;
	}
	// id of function. see more: enum FUNCTION_ID
	private int functionID;
	// parameters
	private String[] args;
	// key: license key
	private String key;
	
	///
	public int getFunctionID() {
		return functionID;
	}
	public void setFunctionID(int functionID) {
		this.functionID = functionID;
	}
	
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}	
}
