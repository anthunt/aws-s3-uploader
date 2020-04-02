package com.anthunt.aws.s3uploader.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import com.anthunt.aws.s3uploader.config.model.Services;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigHelper {

	public static Services loadConfigJSON(URL configJsonURL) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(configJsonURL, Services.class);
	}
	
	public static void loadLog4J2ConfigXML(URL configLog4j2URL) throws URISyntaxException {
		
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(configLog4j2URL.toURI());
		
	}
}
