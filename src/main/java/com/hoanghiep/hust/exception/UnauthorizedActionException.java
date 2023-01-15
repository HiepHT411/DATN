package com.hoanghiep.hust.exception;

public class UnauthorizedActionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedActionException() {
		super();
	}

	public UnauthorizedActionException(String message) {
		super(message);
	}
}
