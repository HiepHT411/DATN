package com.hoanghiep.hust.service;

import com.hoanghiep.hust.entity.User;

public interface IRegistrationService {

	User startRegistration(User user);

	User continueRegistration(User user, String token);

	boolean isRegistrationCompleted(User user);
}
