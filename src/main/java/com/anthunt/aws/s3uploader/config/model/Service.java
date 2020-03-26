package com.anthunt.aws.s3uploader.config.model;

import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class Service {
	
	private static final String DEFAULT_UPLOAD_DIRECTORY = "/upload";
	private static final String DEFAULT_COMPLETE_DIRECTORY = "/complete";
	private static final String DEFAULT_TODAY_FORMAT = "yyyy-MM-dd";
	
	private String name;
	private int readLimit;
	private int bandwidthLimit;
	private long sleepTime;
	private String uploadDirectory;
	private String completeDirectory;
	private boolean useMD5CheckSum;
	private boolean deleteCompleted;
	private String todayFormat;
	
	private S3Access s3Access;
	private List<Directory> directories;
	
	public Service() {
		
		this.setUploadDirectory(DEFAULT_UPLOAD_DIRECTORY);
		this.setCompleteDirectory(DEFAULT_COMPLETE_DIRECTORY);
		this.setUseMD5CheckSum(false);
		this.setDeleteCompleted(true);
		this.setTodayFormat(DEFAULT_TODAY_FORMAT);
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getReadLimit() {
		return readLimit;
	}
	
	public void setReadLimit(int readLimit) {
		this.readLimit = readLimit;
	}
	
	public int getBandwidthLimit() {
		return bandwidthLimit;
	}
	
	public void setBandwidthLimit(int bandwidthLimit) {
		this.bandwidthLimit = bandwidthLimit;
	}
	
	public long getSleepTime() {
		return sleepTime;
	}
	
	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	public String getUploadDirectory() {
		return !uploadDirectory.startsWith("/") ? "/" + uploadDirectory : uploadDirectory;
	}
	
	public void setUploadDirectory(String uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}
	
	public String getCompleteDirectory() {
		return !completeDirectory.startsWith("/") ? "/" + completeDirectory : completeDirectory;
	}
	
	public void setCompleteDirectory(String completeDirectory) {
		this.completeDirectory = completeDirectory;
	}
	
	public boolean isUseMD5CheckSum() {
		return useMD5CheckSum;
	}

	public void setUseMD5CheckSum(boolean useMD5CheckSum) {
		this.useMD5CheckSum = useMD5CheckSum;
	}

	public boolean isDeleteCompleted() {
		return deleteCompleted;
	}

	public void setDeleteCompleted(boolean deleteCompleted) {
		this.deleteCompleted = deleteCompleted;
	}

	public String getTodayFormat() {
		return todayFormat;
	}

	public void setTodayFormat(String todayFormat) {
		this.todayFormat = todayFormat;
	}

	public S3Access getS3Access() {
		return s3Access;
	}
	
	public AmazonS3 getAmazonS3() {
		
        ClientConfiguration clientCfg = new ClientConfiguration();
        if(this.s3Access.getProxy() != null) {
        	if(this.s3Access.getProxy().getSocketBufferSizeHints() != null) {
        		clientCfg.setSocketBufferSizeHints(
        				this.s3Access.getProxy().getSocketBufferSizeHints().getSocketSendBufferSizeHint()
        				, this.s3Access.getProxy().getSocketBufferSizeHints().getSocketReceiveBufferSizeHint()
        		);
        	}
        	if(this.s3Access.getProxy().getTimeout() != null) {
        		clientCfg.setSocketTimeout(this.s3Access.getProxy().getTimeout());
        	}
	        clientCfg.setProtocol(Protocol.valueOf(this.s3Access.getProxy().getProtocol()));
	        clientCfg.setProxyHost(this.s3Access.getProxy().getHost());
	        clientCfg.setProxyPort(this.s3Access.getProxy().getPort());
        }
        
        return new AmazonS3Client(
        		new BasicAWSCredentials(this.s3Access.getAccessKey(), this.s3Access.getSecretKey())
        		, clientCfg
        );        
        
	}
	
	public void setS3Access(S3Access s3Access) {
		this.s3Access = s3Access;
	}
	
	public List<Directory> getDirectories() {
		return directories;
	}
	
	public void setDirectories(List<Directory> directories) {
		this.directories = directories;
	}
	
}
