package com.anthunt.aws.s3uploader.config.model;

public class S3Access {

	private String bucketName;
	private String accessKey;
	private String secretKey;
	private Proxy proxy;
	
	public String getBucketName() {
		return bucketName;
	}
	
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
	public String getAccessKey() {
		return accessKey;
	}
	
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	
	public String getSecretKey() {
		return secretKey;
	}
	
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	public boolean hasProxy() {
		return this.proxy == null ? false : true;
	}
	
	public Proxy getProxy() {
		return proxy;
	}
	
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
	
}
