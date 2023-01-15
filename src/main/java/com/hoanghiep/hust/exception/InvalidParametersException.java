package com.hoanghiep.hust.exception;

public class InvalidParametersException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidParametersException() {
		super();
	}

	public InvalidParametersException(String message) {
		super(message);
	}
}
