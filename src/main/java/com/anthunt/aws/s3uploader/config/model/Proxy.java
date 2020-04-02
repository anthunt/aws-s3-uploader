package com.anthunt.aws.s3uploader.config.model;

public class Proxy {

	private String protocol;
	private String host;
	private int port;
	private String username;
	private String password;
	
	public String getProtocol() {
		return protocol.toUpperCase();
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
	
}
