package com.tokensigning.common;

public class Connector {
	public Connector(int functionID, String[] args) {
		this.functionID = functionID;
		this.args = args;
	}
	//
	private int functionID;
	private String[] args;
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
