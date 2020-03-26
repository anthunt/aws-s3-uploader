package com.anthunt.aws.s3uploader.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.xml.DOMConfigurator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.anthunt.aws.s3uploader.config.model.Services;

public class ConfigLoader {

	public static Services loadConfigJSON(File configJsonFile) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(configJsonFile, Services.class);
	}
	
	public static void loadLog4JConfigXML(URL configLog4jURL) {
			DOMConfigurator.configure(configLog4jURL);
		
	}
}
