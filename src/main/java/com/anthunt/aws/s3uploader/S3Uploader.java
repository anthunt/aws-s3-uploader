package com.anthunt.aws.s3uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthunt.aws.s3uploader.config.model.Directory;
import com.anthunt.aws.s3uploader.config.model.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

public class S3Uploader {

    private Logger log = LoggerFactory.getLogger(S3Uploader.class);

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
		
		log.debug("Data Files Directory : {}", rootDir.getAbsolutePath());
		if(rootDir.isDirectory()) {
			log.debug("Data Files Directory is Exists !");
			
			File[] files = rootDir.listFiles();
			for (File file : files) {
				
				if(findedFiles.size() == this.service.getReadLimit()) {
					break;
				}
				
				log.debug("Use MD5 CheckSum Check [{}]", this.service.isUseMD5CheckSum());
				if(this.service.isUseMD5CheckSum()) {
					
					String fileName = file.getName();
					log.debug("File Name : {}", fileName);
					
					if(fileName.endsWith(".md5")) {
						log.debug("Found MD5 File ! : {}", fileName);
						
						String dataFileName = fileName.replaceAll(".md5", "");
						log.debug("Data File Name : {}", dataFileName);
						
						File dataFile = new File(directory.getSourceDirectory(), dataFileName);
						log.debug("Data File Path : {}", dataFile.getAbsolutePath());
						log.debug("Data file exists : {}", dataFile.exists());
						
						if(dataFile.exists()) {
							
							log.debug("Found Data File ! : {}", dataFile.getAbsolutePath());
							
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
			log.info("[OK] makeFileList : {}", findedFiles.size());
		} else {
			log.error("makeFIleList - Dir not exist : {}", directory.getSourceDirectory());
		}
		return findedFiles;
	}

	private List<File> moveFilesToUploadDirectory(Directory directory, List<File> fileList) {
		List<File> movedFiles = new ArrayList<File>();
		for (File file : fileList) {
			File targetDir = new File(directory.getSourceDirectory() + this.service.getUploadDirectory());
			File targetFile = new File(targetDir, file.getName());
			if(targetFile.exists()) {
				log.error("moveFilesToUploadDirectory - duplicate file : {}", file.getName());
			} else {
				try {
					FileUtils.moveFileToDirectory(file, targetDir, true);
				} catch (IOException e) {
					log.error("moveFilesToUploadDirectory - don't move : {}, reason : {}", file.getName() , e.getMessage());
				}
			}
			movedFiles.add(targetFile);
		}
		return movedFiles;
	}
	
	private void uploadFiles(Directory directory, List<File> fileList) {
		for (File file : fileList) {
			log.info("[OK] uploadFile Start : {}", file.getName());
			try {
				this.uploadFile(directory, file);
				this.moveCompleteFile(directory, file);
				log.info("[OK] uploadFile End : {}", file.getName());
			} catch (Exception e) {
				this.restoreUploadFile(directory, file);
				log.error("uploadFile - upload fail : {}, reason : {}", file.getName(), e.getMessage());
				e.printStackTrace();
			}
		}
		
		this.deleteCompleteFiles(directory);
	}
	
	private String getS3ObjectKey(String objectKeyFormat, String fileName) {
		return objectKeyFormat.replaceAll(TODAY_FORMAT, today).replaceAll(FILE_NAME_FORMAT, fileName);
	}
	
	private void uploadFile(Directory directory, File file) throws Exception {
        
		S3Client s3 = this.service.getAmazonS3();
        
        String bucketName = this.service.getS3Access().getBucketName();
        String objectKeyName = this.getS3ObjectKey(directory.getTargetS3KeyFormat(), file.getName());
        
        // Step 1: Initialize.
        CreateMultipartUploadResponse createMultipartUploadResponse = s3.createMultipartUpload(
        		CreateMultipartUploadRequest.builder()
        										.bucket(bucketName)
        										.key(objectKeyName)
        								    .build()
        );
        
        String uploadId = createMultipartUploadResponse.uploadId();
                
        int partSize = (int) Math.round(((double)(this.service.getBandwidthLimit()) * 1024 * 1024)); // Set part size(min 5 MB)
        
        FileInputStream fileInputStream = null;
        
        try {
        	
        	fileInputStream = new FileInputStream(file);
        	
            // Step 2: Upload parts.
        	Collection<CompletedPart> parts = new ArrayList<>();
        	
        	byte[] bytes = new byte[partSize];
        	
            int readBytes = 0;
            int partNumber = 1;
            while((readBytes = fileInputStream.read(bytes)) != -1) {
            	                
                // Create request to upload a part.
                UploadPartRequest uploadRequest = UploadPartRequest.builder()
			                										.bucket(bucketName)
			                										.key(objectKeyName)
			                										.uploadId(uploadId)
			                										.partNumber(partNumber)
			                										.build();
    
                // Upload part and add response to our list.
                parts.add(
                		CompletedPart.builder()
                					 .eTag(
                							 s3.uploadPart(
                									 uploadRequest
                									 , RequestBody.fromByteBuffer(
                											 ByteBuffer.wrap(bytes, 0, readBytes)
                									 )
                							 ).eTag()
                					 )
                					 .partNumber(partNumber)
                					 .build()
                );
                partNumber++;
                log.debug("Upload : {} bytes", readBytes);
                Thread.sleep(this.service.getSleepTime() * 1000);
            }
    
            // Step 3: complete.                    
            CompleteMultipartUploadResponse completeMultipartUploadResponse = s3.completeMultipartUpload(
            		CompleteMultipartUploadRequest.builder()
            									  .bucket(bucketName)
            									  .key(objectKeyName)
            									  .uploadId(uploadId)
            									  .multipartUpload(
            											  CompletedMultipartUpload.builder()
            											  						  .parts(parts)
            											                          .build()
            									  )
            									  .build()
            );

            log.debug("Uploaded : {}", completeMultipartUploadResponse.bucket());
            
        } catch (Exception e) {
            s3.abortMultipartUpload(
            		AbortMultipartUploadRequest.builder()
	            								   .bucket(bucketName)
	            								   .key(objectKeyName)
	            								   .uploadId(uploadId)
            								   .build()
            );
            throw new Exception("Exception " + e.toString() + " caught");
        } finally {
			if(fileInputStream != null) { try { fileInputStream.close(); } catch(Exception skip) {} }
		}

    }
    
	private void restoreUploadFile(Directory directory, File file) {
		try {
			FileUtils.moveFileToDirectory(file, new File(directory.getSourceDirectory()), true);
			log.warn("restoreUploadFile - move restore file success : {}", file.getName());
		} catch (IOException e) {
			log.error("restoreUploadFile - move restore file fail : {}", e.getMessage());
		}
	}
    
	private void moveCompleteFile(Directory directory, File file) {
		try {
			FileUtils.moveFileToDirectory(file, new File(directory.getSourceDirectory() + this.service.getCompleteDirectory()), true);
		} catch (IOException e) {
			log.error("completeFile - move complete file fail : {}", e.getMessage());
		}
	}

	private void deleteCompleteFiles(Directory directory) {
		try {
			FileUtils.forceDeleteOnExit(new File(directory.getSourceDirectory() + this.service.getUploadDirectory()));
		} catch (IOException e) {
			log.error("delete upload directory fail : {}", e.getMessage());
		}
		
		if(this.service.isDeleteCompleted()) {
			try {
				FileUtils.forceDeleteOnExit(new File(directory.getSourceDirectory() + this.service.getCompleteDirectory()));
			} catch (IOException e) {
				log.error("delete complete directory fail : {}", e.getMessage());
			}
		}
	}
	
}
