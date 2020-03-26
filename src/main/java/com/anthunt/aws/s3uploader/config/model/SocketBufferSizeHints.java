package com.anthunt.aws.s3uploader.config.model;

public class SocketBufferSizeHints {

	private int socketSendBufferSizeHint;
	private int socketReceiveBufferSizeHint;
	
	public int getSocketSendBufferSizeHint() {
		return socketSendBufferSizeHint;
	}
	
	public void setSocketSendBufferSizeHint(int socketSendBufferSizeHint) {
		this.socketSendBufferSizeHint = socketSendBufferSizeHint;
	}
	
	public int getSocketReceiveBufferSizeHint() {
		return socketReceiveBufferSizeHint;
	}
	
	public void setSocketReceiveBufferSizeHint(int socketReceiveBufferSizeHint) {
		this.socketReceiveBufferSizeHint = socketReceiveBufferSizeHint;
	}

}
