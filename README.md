# s3-uploader Configuration Guide

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/11e91acf2571415bb21bd3ce9ae08638)](https://www.codacy.com/gh/anthunt/aws-s3-uploader/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=anthunt/aws-s3-uploader&amp;utm_campaign=Badge_Grade)
![Maven Package](https://github.com/anthunt/aws-s3-uploader/workflows/Maven%20Package/badge.svg)

---

AWS has an Object storage service called S3.

You can implement it for each program by using the SDK directly in the application, but there may be situations where you simply need to periodically upload a file from somewhere to S3.

You can also upload files through log collectors such as Filebeat and fluentD. However, log collectors are designed to be limitedly used for text files.
So, I needed an S3 upload module that could work with any type of file, so I made s3-uploader.

In addition, in the case of S3, it is necessary to know when the writing of the file is completed in order to upload a complete file because it works only in the unit of object of the file.

s3-uploader uses the MD5 file to know when the file write is complete.
When writing a file is completed, an MD5 file is created for the file, and it notifies s3-uploader of the completion of writing the file so that it can recognize that the file has been uploaded.

---

## Deploy

 1. pom.xml : run <code>maven install</code>   
 2. Confirm creation of <code>s3-uploader-\[yyyyMMddHHmiss\].zip</code> file in target directory  
 3. Extract and use the generated zip file in the path you want to run   
 	4. Structure after extracting zip file   

  * **<code>s3-uploader-\[yyyyMMddHHmiss\].jar</code> :** jar for running s3-uploader   
  * **run.sh :** Sample run shell script   
  * **config :** log4j2, s3.uploader preference file directory   
  * **lib :** Reference library jar file directory when executing s3-uploader (program is not recognized when changing)   

## Execution

  * **Running Command :** <code>java -jar s3-uploader-\[yyyyMMddHHmiss\].jar \[options\]</code>   
  * **options :**   

`-help` : Option help   
`-conf <directory>` : Change configuration file directory path   
`-service <serviceName>` : If the name of service to be executed is not entered, service is not performed   
`-serverType <server type>` : Separator for configuration file branches   

  * When executed, the logs directory is created in the execution location and log files are saved. (Can be changed in log4j2.xml settings)   

## JSON Settings Description

  * Optional configuration     

 1. uploadDirectory    
 2. completeDirectory   
 3. useMD5CheckSum   
 4. deleteCompleted     
 5. todayFormat   
 6. proxy    

  * Full configuration - <code>File Name : s3.uploader.json or s3.uploader.\[serverType\].json</code>   

<pre>
	{
		"services" : [ 
			{
				"name" : "Service Name",
				"readLimit" : Number of files to read(int),
				"bandwidthLimit" : Network bandwith(MB, int),
				"sleepTime" : Transmission sleep time per multi-part(Seconds, int),
				"uploadDirectory" : "Directory path for temporary upload files(Default : /upload)",
				"completeDirectory" : "Directory path for temporary completion files(Default : /complete)",
			   "useMD5CheckSum" : [true|false] Whether to check .md5 file creation (Default : false),
				"deleteCompleted": [true|false] Whether to delete the completed file (Default : true),
				"todayFormat": "java SimpleDateFormat String pattern for {today} variable (Default : yyyy-MM-dd)",
				"s3Access" : {
					"bucketName" : "S3 Bucket Name",
	      			"profileName" : "profileName - If profileName is not null, accesskey and secretKey are ignored",
					"accessKey" : "AWS IAM AccessKey",
					"secretKey" : "AWS IAM SecretKey",
					"proxy" : {
						"protocol" : "Proxy protocol - HTTP, HTTPS, TCP, UDP",
						"host" : "Proxy Host Address",
						"port" : Proxy Port Number(int),
						"username" : "Proxy Username",
	        			"password" : "Proxy password"
					}
				},
				"directories" : [ 
					{
						"sourceDirectory" : "Transfer file storage directory",
						"targetS3KeyFormat" : "S3 Object Key format - {today} : Convert to yyyy-MM-dd execution date Can be changed with todayFormat setting, {fileName} : Convert to transfer file name"
					} 
				]
			} 
		]
	}
</pre>

## Logging configuration
 
 * Set to <code>"File Name : log4j2.xml or log4j2.\[serverType\].xml"</code> file under config path.   
 * If there is no log setting under the config path, the default log4j2.xml setting set in the jar program is used.   
