package com.hoanghiep.hust.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.service.IUserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserManagementController {

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
	public String userProfile(Model model) {
		User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		model.addAttribute("user", user);
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

	@GetMapping(value = "/delete/{user_id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public String delete(@PathVariable Long user_id, Model model) {
		userService.delete(user_id);
		return getAllUser(model, 1, 10, "id", "desc");
	}

	@GetMapping(value =  "/all")
	@PreAuthorize("hasRole('ADMIN')")
	public String getAllUser(Model model, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
							 @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
							 @RequestParam(value = "sortField", required = false, defaultValue = "id") String sortField,
							 @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
		Page<User> pageUser = userService.getAllUsers(pageNo, pageSize, sortField, sortDir);

		model.addAttribute("users", pageUser.getContent());
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", pageUser.getTotalPages());
		model.addAttribute("totalItems", pageUser.getTotalElements());

		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		return "userManagement";
	}

	@GetMapping("/showFormForUpdateUser/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public String showFormForUpdateUser(@PathVariable(value = "id") long id, Model model) {
		User user = userService.find(id);

		model.addAttribute("user", user);
		return "updateUser";
	}

	@PostMapping("/update/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public String updatePart(@PathVariable("id") long id, @Valid User user,
							 BindingResult result, Model model) {
		if (result.hasErrors()) {
			user.setId(id);
			return "updateUser";
		}
		user.setId(id);
		User newUser = userService.updateUser(user);

		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		if (Objects.nonNull(authorities)) {
			Collection<String> a = authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
			if (a.contains("ROLE_ADMIN")) {
				return "redirect:/user/all";
			}
		}
		model.addAttribute("user", newUser);
		return "userProfile";
	}

	@RequestMapping(value = "/logout")
	@PreAuthorize("permitAll()")
	@ResponseStatus(HttpStatus.OK)
	public void logout() {
		log.debug("Logged out");
		//return "home";
	}
}
