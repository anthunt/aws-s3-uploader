package com.anthunt.aws.s3uploader.config.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class CommandArguments {
	
	private Logger log = Logger.getLogger(CommandArguments.class);
	
	private static final String DEFAULT_CONFIG_DIRECTORY = "config";
	private static final String JSON_CONFIG_FILE_NAME = "s3.uploader";
	private static final String JSON_CONFIG_FILE_EXTENTION = ".json";
	private static final String LOG4J_CONFIG_FILE_NAME = "log4j";
	private static final String LOG4J_CONFIG_FILE_EXTENTION = ".xml";
	
	private String configDirectory;
	private String service;
	private String serverType;
	
	public CommandArguments() {
		this.setConfigDirectory(DEFAULT_CONFIG_DIRECTORY);
	}
	
	public String getConfigDirectory() {
		log.trace("Config Directory Path [" + this.configDirectory + "]");
		return this.configDirectory;
	}
	
	public void setConfigDirectory(String configDirectory) {
		this.configDirectory = configDirectory;
	}
	
	public File getJsonConfigFile() {
		return new File(this.getConfigDirectory(), JSON_CONFIG_FILE_NAME + this.getServerType() + JSON_CONFIG_FILE_EXTENTION); 
	}
	
	public URL getLog4jConfigURL() {
		
		URL log4jConfigURL = null;
		
		File log4jConfigXML = new File(this.getConfigDirectory(), LOG4J_CONFIG_FILE_NAME + this.getServerType() + LOG4J_CONFIG_FILE_EXTENTION);
		File log4jConfigLocalXML = new File(this.getConfigDirectory(), LOG4J_CONFIG_FILE_NAME + LOG4J_CONFIG_FILE_EXTENTION);
		if(log4jConfigXML.exists()) {
			try {
				log4jConfigURL = log4jConfigXML.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else if(log4jConfigLocalXML.exists()) {
			try {
				log4jConfigURL = log4jConfigLocalXML.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			log4jConfigURL = this.getClass().getClassLoader().getResource("config/log4j.xml");
		}
		
		return log4jConfigURL;
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
					   .append(", jsonConfigFile:").append(this.getJsonConfigFile().getPath())
					   .append(", log4jConfigFile:").append(this.getLog4jConfigURL().toString());
		
		return toStringBuilder.toString();
	}
	
}
