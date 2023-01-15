package com.hoanghiep.hust.exception;

public class ActionRefusedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ActionRefusedException() {
		super();
	}

	public ActionRefusedException(String message) {
		super(message);
	}
}