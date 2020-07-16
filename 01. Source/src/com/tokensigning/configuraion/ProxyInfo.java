package com.tokensigning.configuraion;

/**
* ProxyInfo: object define file proxy info
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class ProxyInfo {
	private String server;
	private String port;
	private String userName;
	private String password;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
}
