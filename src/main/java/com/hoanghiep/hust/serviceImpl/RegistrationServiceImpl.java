package com.hoanghiep.hust.serviceImpl;

import com.hoanghiep.hust.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.service.IRegistrationService;
import com.hoanghiep.hust.service.IUserService;

import java.util.Arrays;
import java.util.Objects;

@Service
public class RegistrationServiceImpl implements IRegistrationService {

	@Autowired
	private IUserService userService;
	
	@Override
	public User startRegistration(User user) {
		User newUser = null;
		if (Objects.nonNull(user)) {
			user.setRoles(Arrays.asList(Role.ROLE_USER));
			newUser = userService.saveUser(user);
			newUser.setEnabled(true); //auto completed registration
			//userService.setRegistrationCompleted(user);
		}
		return newUser;
	}

	@Override
	public User continueRegistration(User user, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRegistrationCompleted(User user) {
		// TODO Auto-generated method stub
		return userService.isRegistrationCompleted(user);
	}

}
