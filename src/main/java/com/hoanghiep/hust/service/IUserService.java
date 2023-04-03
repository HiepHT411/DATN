package com.hoanghiep.hust.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.exception.ResourceUnavailableException;
import com.hoanghiep.hust.exception.UnauthorizedActionException;
import com.hoanghiep.hust.exception.UserAlreadyExistsException;

public interface IUserService extends UserDetailsService {
	User saveUser(User user) throws UserAlreadyExistsException;

	User find(Long id) throws ResourceUnavailableException;

	User findByEmail(String email) throws ResourceUnavailableException;

	User findByUsername(String username) throws ResourceUnavailableException;

	User updatePassword(User user, String password) throws ResourceUnavailableException;

	void delete(Long user_id) throws UnauthorizedActionException, ResourceUnavailableException;

	User setRegistrationCompleted(User user) throws ResourceUnavailableException;

	boolean isRegistrationCompleted(User user) throws ResourceUnavailableException;

    Page<User> getAllUsers(int pageNo, int pageSize, String sortField, String sortDir);

	User updateUser(User updatedUser);
}