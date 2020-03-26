#s3-uploader Configuration Guide

## 프로그램 배포

>
1. pom.xml - maven install 실행 
2. target 디렉토리내에 s3-uploader-[yyyyMMddHHmiss].zip 파일 생성 확인
3. 생성된 zip 파일을 실행하기 원하는 경로에 압축 해제 후 사용
4. zip 파일 압축 해제 후 구조

>>
* **s3-uploader-[yyyyMMddHHmiss].jar :** s3-uploader 실행 용 jar
* **run.sh :** 실행 Shell 스크립트 샘플
* **config :** log4j, s3.uploader 기본 설정 파일 디렉토리
* **lib :** s3-uploader 실행 시 참조 라이브러리 jar 파일 디렉토리 (변경 시 프로그램에서 인식 못함)

## 프로그램 실행

>
* **실행 문 :** `java -jar s3-uploader-[yyyyMMddHHmiss].jar [options]`
* **options :**

>>
* `-help` : 옵션 도움말
* `-conf <directory>` : 설정 파일 디렉토리 경로 변경
* `-service <serviceName>` : 실행 대상 서비스 명, 미 입력 시 서비스 미 수행
* `-serverType <server type>` : 설정 파일 분기용 구분자

>
* 실행 시 실행 위치에 logs 디렉토리가 생성되며 로그 파일이 저장 됨. (log4j.xml 설정에서 변경 가능)

## JSON 설정 설명

* 생략 가능 설정

> 
1. uploadDirectory
2. completeDirectory
3. useMD5CheckSum
4. deleteCompleted
5. todayFormat
6. proxy

* 전체 설정 - `파일 명 : s3.uploader.json 혹은 s3.uploader.[serverType].json` 

>
	{
		"services" : [ 
			{
				"name" : "서비스 명",
				"readLimit" : 읽을 파일 수(int),
				"bandwidthLimit" : 네트워크 bandwith(MB단위, int),
				"sleepTime" : 멀티파트별 전송 sleep 시간(초단위, int),
				"uploadDirectory" : "임시 업로드 파일 용 디렉토리 경로(기본 : /upload)",
				"completeDirectory" : "임시 완료 파일 용 디렉토리 경로(기본 : /complete)",
			   "useMD5CheckSum" : [true|false] .md5 파일 생성 체크 여부 (기본 : false),
				"deleteCompleted": [true|false] 완료 파일 삭제 여부 (기본 : true),
				"todayFormat": "{today} 변수에 대한 java SimpleDateFormat String pattern (기본 : yyyy-MM-dd)",
				"s3Access" : {
					"bucketName" : "S3 버킷 명",
					"accessKey" : "AWS IAM 계정 AccessKey",
					"secretKey" : "AWS IAM 계정 SecretKey",
					"proxy" : {
						"protocol" : "Proxy 프로토콜 - HTTP, HTTPS, TCP, UDP",
						"host" : "Proxy Host 주소",
						"port" : Proxy Port 번호(int),
						"timeout" : Proxy Timeout 시간(ms단위, int),
						"socketBufferSizeHints" : {
							"socketSendBufferSizeHint" : socket send buffer size(byte단위, int),
							"socketReceiveBufferSizeHint" : socket receive buffer size(byte단위, int)
						}
					}
				},
				"directories" : [ 
					{
						"sourceDirectory" : "전송 파일 저장 디렉토리",
						"targetS3KeyFormat" : "S3 Object Key 포맷 - {today} : yyyy-MM-dd 실행 일자로 변환 todayFormat 설정으로 변경 가능, {fileName} : 전송 파일 명으로 변환"
					} 
				]
			} 
		]
	}

## 로그 설정

>
* config 경로 하위에 `파일 명 : log4j.xml 혹은 log4j.[serverType].xml` 파일로 설정
* config 경로 하위에 로그 설정이 없을 경우 jar 프로그램 내부에 설정된 기본 log4j.xml 설정을 사용함.
