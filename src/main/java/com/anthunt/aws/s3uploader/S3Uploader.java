package com.anthunt.aws.s3uploader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.anthunt.aws.s3uploader.config.model.Directory;
import com.anthunt.aws.s3uploader.config.model.Service;

public class S3Uploader {

    private Logger log = Logger.getLogger(S3Uploader.class);

    private static final String TODAY_FORMAT = "[{]today[}]";
    private static final String FILE_NAME_FORMAT = "[{]fileName[}]";
    
    private String today;
    private Service service;
    
	public S3Uploader(Service service) {
		this.service = service;
		SimpleDateFormat format = new SimpleDateFormat(this.service.getTodayFormat());
		this.today = format.format(new Date());
	}
	
	public void run() {
		List<Directory> directories = this.service.getDirectories();
		for (Directory directory : directories) {
			List<File> fileList = this.findFileList(directory);
			if(fileList.size() > 0) {
				List<File> movedFiles = this.moveFilesToUploadDirectory(directory, fileList);
				this.uploadFiles(directory, movedFiles);
			}
		}
		
	}
	
	private List<File> findFileList(Directory directory) {
		List<File> findedFiles = new ArrayList<File>();
		File rootDir = new File(directory.getSourceDirectory());
		
		log.debug("Data Files Directory : " + rootDir.getAbsolutePath());
		if(rootDir.isDirectory()) {
			log.debug("Data Files Directory is Exists !");
			
			File[] files = rootDir.listFiles();
			for (File file : files) {
				
				if(findedFiles.size() == this.service.getReadLimit()) {
					break;
				}
				
				log.debug("Use MD5 CheckSum Check [" + this.service.isUseMD5CheckSum() + "]");
				if(this.service.isUseMD5CheckSum()) {
					
					String fileName = file.getName();
					log.debug("File Name : " + fileName);
					
					if(fileName.endsWith(".md5")) {
						log.debug("Found MD5 File ! : " + fileName);
						
						String dataFileName = fileName.replaceAll(".md5", "");
						log.debug("Data File Name : " + dataFileName);
						
						File dataFile = new File(directory.getSourceDirectory(), dataFileName);
						log.debug("Data File Path : " + dataFile.getAbsolutePath());
						log.debug(dataFile.exists());
						
						if(dataFile.exists()) {
							
							log.debug("Found Data File ! : " + dataFile.getAbsolutePath());
							
							findedFiles.add(dataFile);
							findedFiles.add(file);
							
						} else {
							
							log.warn("Can not find Data File !");
							
						}
						
					} else {
						log.debug("This file is not MD5 File !");
					}
					
				} else {
					findedFiles.add(file);
				}
			}
			log.info("[OK] makeFileList : " + findedFiles.size());
		} else {
			log.error("makeFIleList - Dir not exist :" + directory.getSourceDirectory());
		}
		return findedFiles;
	}

	private List<File> moveFilesToUploadDirectory(Directory directory, List<File> fileList) {
		List<File> movedFiles = new ArrayList<File>();
		for (File file : fileList) {
			File targetDir = new File(directory.getSourceDirectory() + this.service.getUploadDirectory());
			File targetFile = new File(targetDir, file.getName());
			if(targetFile.exists()) {
				log.error("moveFilesToUploadDirectory - duplicate file : " + file.getName());
			} else {
				try {
					FileUtils.moveFileToDirectory(file, targetDir, true);
				} catch (IOException e) {
					log.error("moveFilesToUploadDirectory - don't move : " + file.getName() + ", reason : " + e.getMessage());
				}
			}
			movedFiles.add(targetFile);
		}
		return movedFiles;
	}
	
	private void uploadFiles(Directory directory, List<File> fileList) {
		for (File file : fileList) {
			log.info("[OK] uploadFile Start : " + file.getName());
			try {
				this.uploadFile(directory, file);
				this.moveCompleteFile(directory, file);
				log.info("[OK] uploadFile End : " + file.getName());
			} catch (Exception e) {
				this.restoreUploadFile(directory, file);
				log.error("uploadFile - upload fail : " + file.getName() + ", reason : " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		this.deleteCompleteFiles(directory);
	}
	
	private String getS3ObjectKey(String objectKeyFormat, String fileName) {
		return objectKeyFormat.replaceAll(TODAY_FORMAT, today).replaceAll(FILE_NAME_FORMAT, fileName);
	}
	
	private void uploadFile(Directory directory, File file) throws Exception {
        		
        AmazonS3 s3 = this.service.getAmazonS3();
        
        String bucketName = this.service.getS3Access().getBucketName();
        String objectKeyName = this.getS3ObjectKey(directory.getTargetS3KeyFormat(), file.getName());
        
        // for each part upload.
        List<PartETag> partETags = new ArrayList<PartETag>();
    
        // Step 1: Initialize.
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, objectKeyName);
        InitiateMultipartUploadResult initResponse = s3.initiateMultipartUpload(initRequest);
        
        long contentLength = file.length();
        long partSize =  Math.round(((double)(this.service.getBandwidthLimit()) * 1024 * 1024)); // Set part size(min 5 MB)
        
        try {
            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Last part can be less than partSize. Adjust part size.
                partSize = Math.min(partSize, (contentLength - filePosition));
                    
                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                										.withBucketName(bucketName)
                										.withKey(objectKeyName)
                										.withUploadId(initResponse.getUploadId())
                										.withPartNumber(i)
                										.withFileOffset(filePosition)
                										.withFile(file)
                										.withPartSize(partSize);
    
                // Upload part and add response to our list.
                partETags.add(s3.uploadPart(uploadRequest).getPartETag());
                filePosition += partSize;
                log.debug("Upload " + filePosition);
                Thread.sleep(this.service.getSleepTime() * 1000);
            }
    
            // Step 3: complete.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, objectKeyName, initResponse.getUploadId(), partETags);
                    
            s3.completeMultipartUpload(compRequest);

        } catch (Exception e) {
            s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, objectKeyName, initResponse.getUploadId()));
            throw new Exception("Exception " + e.toString() + " caught");
        }

    }
    
	private void restoreUploadFile(Directory directory, File file) {
		try {
			FileUtils.moveFileToDirectory(file, new File(directory.getSourceDirectory()), true);
			log.warn("restoreUploadFile - move restore file success : " + file.getName());
		} catch (IOException e) {
			log.error("restoreUploadFile - move restore file fail : " + e.getMessage());
		}
	}
    
	private void moveCompleteFile(Directory directory, File file) {
		try {
			FileUtils.moveFileToDirectory(file, new File(directory.getSourceDirectory() + this.service.getCompleteDirectory()), true);
		} catch (IOException e) {
			log.error("completeFile - move complete file fail : " + e.getMessage());
		}
	}

	private void deleteCompleteFiles(Directory directory) {
		try {
			FileUtils.forceDeleteOnExit(new File(directory.getSourceDirectory() + this.service.getUploadDirectory()));
		} catch (IOException e) {
			log.error("delete upload directory fail : " + e.getMessage());
		}
		
		if(this.service.isDeleteCompleted()) {
			try {
				FileUtils.forceDeleteOnExit(new File(directory.getSourceDirectory() + this.service.getCompleteDirectory()));
			} catch (IOException e) {
				log.error("delete complete directory fail : " + e.getMessage());
			}
		}
	}
	
}
