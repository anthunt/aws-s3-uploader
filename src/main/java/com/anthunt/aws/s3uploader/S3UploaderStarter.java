package com.anthunt.aws.s3uploader;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthunt.aws.s3uploader.config.ConfigHelper;
import com.anthunt.aws.s3uploader.config.model.CommandArguments;
import com.anthunt.aws.s3uploader.config.model.Service;
import com.anthunt.aws.s3uploader.config.model.Services;

public class S3UploaderStarter {

	private Logger log = LoggerFactory.getLogger(S3UploaderStarter.class);
	
    private Options options;
    
    public S3UploaderStarter(String[] args) {
    	
    	this.setOptions();
    	
    	CommandLineParser parser = new DefaultParser();
    	CommandLine line = null;
    	
    	CommandArguments commandArguments = new CommandArguments();
    	
    	try {

    		line = parser.parse(options, args);

        	if(line.hasOption("conf")) {
        		commandArguments.setConfigDirectory(line.getOptionValue("conf"));
        	}
        	
        	if(line.hasOption("serverType")) {
        		commandArguments.setServerType(line.getOptionValue("serverType"));
        	}
        	
        	if(line.hasOption("service")) {
        		commandArguments.setService(line.getOptionValue("service"));
        	}
        	
        	if(line.hasOption("help")) {
        		this.printHelp();
        		return;
        	}
        	
    	} catch (ParseException e) {
    		System.out.println("Arguments parsing failed.  Reason: " + e.getMessage());
    		e.printStackTrace();
    		this.printHelp();
    		return;
    	}
    			
    	try {
			ConfigHelper.loadLog4J2ConfigXML(commandArguments.getLog4j2ConfigURL());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			System.out.println("Could not load log4j2 configuration file.  Reason: " + e1.getMessage());
    		e1.printStackTrace();
    		this.printHelp();
    		return;
		}

    	log.info("Start S3UploaderStarter");
    	log.info("Running Arguments [{}]", commandArguments.toString());
    	
    	Services services = new Services();
    	
    	try {
    		
			services = ConfigHelper.loadConfigJSON(commandArguments.getJsonConfigURL());
			log.info("loaded json config [Service : " + commandArguments.getService() + "/" + services.getServices().size() + "ea]");
			
		} catch (IOException e) {
			log.error("Load json config failed. Reason: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		for (Service service : services.getServices()) {
			// Run if selected service exists
			if(commandArguments.getService().equals(service.getName())) {
		    	new S3Uploader(service).run();
			}
		}
		
		log.info("Upload Process Completed !");
    	
    }
    
    private void setOptions() {
    	options = new Options();
    	
    	options.addOption(
    			Option.builder("help")
                	.build()
        );
    	
    	options.addOption(
    			Option.builder("conf")
                	.argName("directory")
                	.hasArg()
                	.desc("use given config directory")
                	.build()
        );
    	
    	options.addOption(
    			Option.builder("serverType")
                	.argName("server type")
                	.hasArg()
                	.desc("use given server type")
                	.build()
        );
    	
    	options.addOption(
    			Option.builder("service")
                	.argName("serviceName")
                	.hasArg()
                	.desc("use service name")
                	.build()
        );
    	
    }
        
    private void printHelp() {
    	HelpFormatter formatter = new HelpFormatter();
    	formatter.printHelp( this.getClass().getName(), options );
    }
    
    public static void main(String[] args) {
        try {
        	new S3UploaderStarter(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
