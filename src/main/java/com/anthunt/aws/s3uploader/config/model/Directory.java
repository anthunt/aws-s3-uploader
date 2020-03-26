package com.anthunt.aws.s3uploader.config.model;

public class Directory {
	
	private String sourceDirectory;
	private String targetS3KeyFormat;
	
	public String getSourceDirectory() {
		return sourceDirectory;
	}
	
	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}
	
	public String getTargetS3KeyFormat() {
		return targetS3KeyFormat;
	}
	
	public void setTargetS3KeyFormat(String targetS3KeyFormat) {
		this.targetS3KeyFormat = targetS3KeyFormat;
	}
	
}
