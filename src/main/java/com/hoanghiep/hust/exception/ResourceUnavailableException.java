package com.hoanghiep.hust.exception;

public class ResourceUnavailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceUnavailableException() {
		super();
	}

	public ResourceUnavailableException(String message) {
		super(message);
	}
}
