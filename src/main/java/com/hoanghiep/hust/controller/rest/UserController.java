//package com.hoanghiep.hust.controller.rest;
//
//import javax.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.hoanghiep.hust.entity.User;
//import com.hoanghiep.hust.security.AuthenticatedUser;
//import com.hoanghiep.hust.service.IRegistrationService;
//import com.hoanghiep.hust.service.IUserService;
//import com.hoanghiep.hust.utility.VerifierUtils;
//
//import lombok.extern.slf4j.Slf4j;
//
//@RestController
//@RequestMapping(UserController.USER_MAPPING)
//@Slf4j
//public class UserController {
//
//	public static final String USER_MAPPING = "/api/users";
//
//	@Autowired
//	private IRegistrationService registrationService;
//
//	@Autowired
//	private IUserService userService;
//
//	@PostMapping(value = "/registration")
//	@PreAuthorize("permitAll")
//	public ResponseEntity<User> signUp(@Valid User user, BindingResult result) {
//
//		VerifierUtils.verifyModelResult(result);
//		User newUser = registrationService.startRegistration(user);
//
//		if (registrationService.isRegistrationCompleted(newUser)) {
//			return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<User>(newUser, HttpStatus.OK);
//		}
//	}
//
//	@DeleteMapping(value = "/{user_id}")
//	@PreAuthorize("isAuthenticated()")
//	@ResponseStatus(HttpStatus.OK)
//	public void delete(@PathVariable Long user_id) {
//
//		userService.delete(user_id);
//	}
//
//	@PostMapping(value = "/login")
//	@PreAuthorize("isAuthenticated()")
//	@ResponseStatus(HttpStatus.OK)
//	public User login(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
//		log.debug("Logged in as " + authenticatedUser.getUsername());
//		return authenticatedUser.getUser();
//	}
//
//	@PostMapping(value = "/logoutDummy")
//	@PreAuthorize("permitAll()")
//	@ResponseStatus(HttpStatus.OK)
//	public void logout() {
//		// Dummy endpoint to point Spring Security to
//		log.debug("Logged out");
//	}
//
////	@RequestMapping(value = "/forgotPassword")
////	@PreAuthorize("permitAll")
////	@ResponseStatus(HttpStatus.OK)
////	public User forgotPassword(String email) {
////		User user = userService.findByEmail(email);
////		userManagementService.resendPassword(user);
////
////		return user;
////	}
//}
