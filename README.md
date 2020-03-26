#s3-uploader Configuration Guide

## Deploy

>
1. pom.xml - run "maven install" 
2. Confirm creation of s3-uploader- [yyyyMMddHHmiss] .zip file in target directory
3. Extract and use the generated zip file in the path you want to run
4. Structure after extracting zip file

>>
* **s3-uploader-[yyyyMMddHHmiss].jar :** jar for running s3-uploader
* **run.sh :** Sample run shell script
* **config :** log4j, s3.uploader preference file directory
* **lib :** Reference library jar file directory when executing s3-uploader (program is not recognized when changing)

## Execution

>
* **Running Command :** `java -jar s3-uploader-[yyyyMMddHHmiss].jar [options]`
* **options :**

>>
* `-help` : Option help
* `-conf <directory>` : Change configuration file directory path
* `-service <serviceName>` : If the name of service to be executed is not entered, service is not performed
* `-serverType <server type>` : Separator for configuration file branches

>
* When executed, the logs directory is created in the execution location and log files are saved. (Can be changed in log4j.xml settings)

## JSON Settings Description

* Optional configuration

> 
1. uploadDirectory
2. completeDirectory
3. useMD5CheckSum
4. deleteCompleted
5. todayFormat
6. proxy

* Full configuration - `File Name : s3.uploader.json or s3.uploader.[serverType].json` 

>
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
					"accessKey" : "AWS IAM AccessKey",
					"secretKey" : "AWS IAM SecretKey",
					"proxy" : {
						"protocol" : "Proxy protocol - HTTP, HTTPS, TCP, UDP",
						"host" : "Proxy Host Address",
						"port" : Proxy Port Number(int),
						"timeout" : Proxy Timeout(ms, int),
						"socketBufferSizeHints" : {
							"socketSendBufferSizeHint" : socket send buffer size(byte, int),
							"socketReceiveBufferSizeHint" : socket receive buffer size(byte, int)
						}
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

## Logging configuration

>
* Set to `"File Name : log4j.xml or log4j.[serverType].xml"` file under config path.
* If there is no log setting under the config path, the default log4j.xml setting set in the jar program is used.
