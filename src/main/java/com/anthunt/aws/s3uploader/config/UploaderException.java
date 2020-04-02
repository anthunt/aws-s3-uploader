package com.anthunt.aws.s3uploader.config;

public class UploaderException extends RuntimeException {

	private static final long serialVersionUID = -3408904010083486607L;

	public UploaderException() {
		super();
	}

	public UploaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UploaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public UploaderException(String message) {
		super(message);
	}

	public UploaderException(Throwable cause) {
		super(cause);
	}

}
