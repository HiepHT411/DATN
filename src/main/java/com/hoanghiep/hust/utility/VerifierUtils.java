package com.hoanghiep.hust.utility;

import org.springframework.validation.BindingResult;

import com.hoanghiep.hust.exception.ModelVerificationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerifierUtils {

	public static void verifyModelResult(BindingResult result) throws ModelVerificationException {
		if (result.hasErrors()) {
			log.error(result.toString());
			throw new ModelVerificationException(result.getFieldError().getDefaultMessage());
		}
	}
}
