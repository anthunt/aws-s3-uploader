package com.anthunt.aws.s3uploader.config.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.Protocol;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.services.s3.S3Client;

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
	
	public S3Client getAmazonS3() {
		
		URI endpoint;
		ProxyConfiguration proxyConfiguration = null;

		if(this.s3Access.getProxy() != null) {
			try {
				endpoint = new URI(Protocol.valueOf(this.s3Access.getProxy().getProtocol()) + "://" + this.s3Access.getProxy().getHost() + ":" + this.s3Access.getProxy().getPort());
				proxyConfiguration = ProxyConfiguration.builder()
													   .endpoint(endpoint)
													   .username(this.s3Access.getProxy().getUserName())
													   .password(this.s3Access.getProxy().getPassword())
													   .build();		   
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		SdkHttpClient sdkHttpClient = ApacheHttpClient.builder()
													  .proxyConfiguration(proxyConfiguration)
													  .build();
                     
		AwsCredentialsProvider awsCredentialsProvider = null;
		
		if(this.s3Access.getProfileName() == null) {
			awsCredentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(this.s3Access.getAccessKey(), this.s3Access.getSecretKey()));
		} else {
			awsCredentialsProvider = ProfileCredentialsProvider.create(this.s3Access.getProfileName());
		}
		
        return S3Client.builder()
			        	   .httpClient(sdkHttpClient)
			        	   .credentialsProvider(awsCredentialsProvider)
			        	   .region(this.s3Access.getRegion())
		        	   .build();
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
