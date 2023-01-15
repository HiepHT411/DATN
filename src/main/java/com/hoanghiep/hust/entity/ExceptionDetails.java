package com.hoanghiep.hust.entity;

public class ExceptionDetails {

	public final String url;
	public final String message;
	public final Integer code;

	public ExceptionDetails(String url, Exception ex, Integer code) {
		this.url = url;
		this.message = ex.getLocalizedMessage();
		this.code = code;
	}
}
