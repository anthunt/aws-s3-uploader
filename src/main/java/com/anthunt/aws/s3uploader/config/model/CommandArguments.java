package com.anthunt.aws.s3uploader.config.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandArguments {
	
	private Logger log = LoggerFactory.getLogger(CommandArguments.class);
	
	private static final String DEFAULT_CONFIG_DIRECTORY = "config";
	private static final String JSON_CONFIG_FILE_NAME = "s3.uploader";
	private static final String JSON_CONFIG_FILE_EXTENTION = ".json";
	private static final String LOG4J2_CONFIG_FILE_NAME = "log4j2";
	private static final String LOG4J2_CONFIG_FILE_EXTENTION = ".xml";
	
	private String configDirectory;
	private String service;
	private String serverType;
	
	public CommandArguments() {
		this.setConfigDirectory(DEFAULT_CONFIG_DIRECTORY);
	}
	
	public String getConfigDirectory() {
		log.trace("Config Directory Path [{}]", this.configDirectory);
		return this.configDirectory;
	}
	
	public void setConfigDirectory(String configDirectory) {
		this.configDirectory = configDirectory;
	}
	
	public URL getJsonConfigURL() {
		
		URL jsonConfigURL = null;
		
		File jsonConfigFile = new File(this.getConfigDirectory(), JSON_CONFIG_FILE_NAME + this.getServerType() + JSON_CONFIG_FILE_EXTENTION);
		if(jsonConfigFile.exists()) {
			try {
				jsonConfigURL = jsonConfigFile.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			jsonConfigURL = this.getClass().getClassLoader().getResource("config/" + JSON_CONFIG_FILE_NAME + JSON_CONFIG_FILE_EXTENTION);
		}
		
		return jsonConfigURL; 
	}
	
	public URL getLog4j2ConfigURL() {
		
		URL log4j2ConfigURL = null;
		
		File log4j2ConfigXML = new File(this.getConfigDirectory(), LOG4J2_CONFIG_FILE_NAME + this.getServerType() + LOG4J2_CONFIG_FILE_EXTENTION);
		File log4j2ConfigLocalXML = new File(this.getConfigDirectory(), LOG4J2_CONFIG_FILE_NAME + LOG4J2_CONFIG_FILE_EXTENTION);
		if(log4j2ConfigXML.exists()) {
			try {
				log4j2ConfigURL = log4j2ConfigXML.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else if(log4j2ConfigLocalXML.exists()) {
			try {
				log4j2ConfigURL = log4j2ConfigLocalXML.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			log4j2ConfigURL = this.getClass().getClassLoader().getResource("config/" + LOG4J2_CONFIG_FILE_NAME + LOG4J2_CONFIG_FILE_EXTENTION);
		}
		
		return log4j2ConfigURL;
	}
	
	public String getService() {
		return service == null ? "" : service;
	}
	
	public void setService(String service) {
		this.service = service;
	}

	public String getServerType() {
		return serverType == null ? "" : "." + serverType.toUpperCase();
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	
	public String toString() {
		StringBuilder toStringBuilder = new StringBuilder();
		toStringBuilder.append("configDirectory:").append(this.getConfigDirectory())
					   .append(", service:").append(this.getService())
					   .append(", serverType:").append(this.getServerType())
					   .append(", jsonConfigFile:").append(this.getJsonConfigURL().getPath())
					   .append(", log4j2ConfigFile:").append(this.getLog4j2ConfigURL().toString());
		
		return toStringBuilder.toString();
	}
	
}
