package com.anthunt.aws.s3uploader.config.model;

import software.amazon.awssdk.regions.Region;

public class S3Access {

	private String bucketName;
	private String region;
	private String profileName;
	private String accessKey;
	private String secretKey;
	private Proxy proxy;
	
	public String getBucketName() {
		return bucketName;
	}
	
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
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

	public Region getRegion() {
		return Region.of(this.region);
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
}
