package com.anthunt.aws.s3uploader.config.model;

public class Proxy {

	private String protocol;
	private String host;
	private int port;
	private Integer timeout;
	private SocketBufferSizeHints socketBufferSizeHints;
	
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
	
	public Integer getTimeout() {
		return timeout;
	}
	
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	public SocketBufferSizeHints getSocketBufferSizeHints() {
		return socketBufferSizeHints;
	}
	
	public void setSocketBufferSizeHints(SocketBufferSizeHints socketBufferSizeHints) {
		this.socketBufferSizeHints = socketBufferSizeHints;
	}
		
}
