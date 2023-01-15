package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.security.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.service.IUserService;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserManagementController {

//	@Autowired
//	private UserManagementService userManagementService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private IUserService userService;

	@GetMapping("/login")
	@PreAuthorize("permitAll")
	public String showLoginPage(@ModelAttribute User user) {
		return "login";
	}

//	@PostMapping("/login")
//	@PreAuthorize("isAuthenticated()")
//	public String login(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
//		log.debug("User management controller. Logged in as " + authenticatedUser.getUsername());
//		return "/";
//	}
	
	@RequestMapping(value = "/login-error", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public String loginError(@ModelAttribute User user, Model model) {
		model.addAttribute("loginError", true);
		return "login";
	}

	@GetMapping(value = "/profile")
	@PreAuthorize("isAuthenticated()")
	public String userProfile() {
		return "userProfile";
	}

	@GetMapping(value = "/forgotPassword")
	@PreAuthorize("permitAll")
	public String forgotPassword() {
		return "forgotPassword";
	}

//	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
//	@PreAuthorize("permitAll")
//	public ModelAndView forgotPassword(String email) {
//		User user = userService.findByEmail(email);
//		userManagementService.resendPassword(user);
//
//		ModelAndView mav = new ModelAndView();
//		mav.addObject("header", messageSource.getMessage("label.forgotpassword.success.header", null, null));
//		mav.addObject("subheader", messageSource.getMessage("label.forgotpassword.success.subheader", null, null));
//		mav.setViewName("simplemessage");
//
//		return mav;
//	}
//
//	@RequestMapping(value = "/{user_id}/resetPassword", method = RequestMethod.GET)
//	@PreAuthorize("permitAll")
//	public ModelAndView resetPassword(@PathVariable Long user_id, String token) {
//		User user = userService.find(user_id);
//		userManagementService.verifyResetPasswordToken(user, token);
//
//		ModelAndView mav = new ModelAndView();
//		mav.addObject("user", user);
//		mav.addObject("token", token);
//		mav.setViewName("resetPassword");
//
//		return mav;
//	}
//
//	@RequestMapping(value = "/{user_id}/resetPassword", method = RequestMethod.POST)
//	@PreAuthorize("permitAll")
//	public String resetPassword(@PathVariable Long user_id, String token, String password) {
//		User user = userService.find(user_id);
//		userManagementService.verifyResetPasswordToken(user, token);
//
//		userManagementService.updatePassword(user, password);
//
//		return "login";
//	}

	@DeleteMapping(value = "/{user_id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long user_id) {
		userService.delete(user_id);
	}

	@RequestMapping(value = "/logout")
	@PreAuthorize("permitAll()")
	@ResponseStatus(HttpStatus.OK)
	public void logout() {
		log.debug("Logged out");
		//return "home";
	}
}
