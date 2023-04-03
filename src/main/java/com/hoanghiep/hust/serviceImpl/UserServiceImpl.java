package com.hoanghiep.hust.serviceImpl;

import java.security.InvalidParameterException;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.exception.ResourceUnavailableException;
import com.hoanghiep.hust.exception.UnauthorizedActionException;
import com.hoanghiep.hust.exception.UserAlreadyExistsException;
import com.hoanghiep.hust.repository.UserRepository;
import com.hoanghiep.hust.security.AuthenticatedUser;
import com.hoanghiep.hust.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User saveUser(User user) throws UserAlreadyExistsException {
		if (userRepository.findByEmail(user.getEmail()) != null) {
			log.error("The email address" + user.getEmail() + " is already in use");
			throw new UserAlreadyExistsException("The mail " + user.getEmail() + " is already in use");
		}
		if (userRepository.findByUsername(user.getUsername()) != null) {
			log.error("The username " + user.getUsername() + " has been taken");
			throw new UserAlreadyExistsException("The username " + user.getUsername() + " has been taken");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(false);

		return userRepository.save(user);
	}

	@Override
	public AuthenticatedUser loadUserByUsername(String username) throws UsernameNotFoundException {
		User user;

		try {
			user = findByEmail(username);
		} catch (ResourceUnavailableException e) {
			try {
				user = findByUsername(username);
			} catch (ResourceUnavailableException e2) {
				throw new UsernameNotFoundException(username + " couldn't be resolved to any user");
			}
		}

		return new AuthenticatedUser(user);
	}

	@Override
	public User findByUsername(String username) throws ResourceUnavailableException {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			log.error("The user " + username + " doesn't exist");
			throw new ResourceUnavailableException("The user " + username + " doesn't exist");
		}

		return user;
	}

	@Override
	public User find(Long id) throws ResourceUnavailableException {
		User user = userRepository.findById(id).orElseThrow(() -> new InvalidParameterException("User not found with id: " + id));

		if (user == null) {
			log.error("The user " + id + " can't be found");
			throw new ResourceUnavailableException("User " + id + " not found.");
		}

		return user;
	}

	@Override
	public void delete(Long user_id) throws UnauthorizedActionException, ResourceUnavailableException {
		User userToDelete = find(user_id);

		userRepository.delete(userToDelete);
	}

	@Override
	public User setRegistrationCompleted(User user) {
		user.setEnabled(true);
		return userRepository.save(user);
	}

	@Override
	public boolean isRegistrationCompleted(User user) {
		return user.isEnabled();
	}

	@Override
	public Page<User> getAllUsers(int pageNo, int pageSize, String sortField, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
				Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return userRepository.findAll(pageable);
	}

	@Override
	public User updateUser(User updatedUserDto) {
		User userToBeUpdated = find(updatedUserDto.getId());
		if (Objects.nonNull(updatedUserDto.getUserLevel())) {
			userToBeUpdated.setUserLevel(updatedUserDto.getUserLevel());
		}
		if (Objects.nonNull(updatedUserDto.getPassword())) {
			userToBeUpdated.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
		}
		return userRepository.save(userToBeUpdated);
	}

	@Override
	public User findByEmail(String email) throws ResourceUnavailableException {
		User user = userRepository.findByEmail(email);

		if (user == null) {
			log.error("The mail " + email + " can't be found");
			throw new ResourceUnavailableException("The mail " + email + " can't be found");
		}

		return user;
	}

	@Override
	public User updatePassword(User user, String password) throws ResourceUnavailableException {
		user.setPassword(passwordEncoder.encode(password));
		return userRepository.save(user);
	}

}
