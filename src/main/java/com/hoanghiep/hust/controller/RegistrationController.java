package com.hoanghiep.hust.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.exception.ModelVerificationException;
import com.hoanghiep.hust.exception.UserAlreadyExistsException;
import com.hoanghiep.hust.service.IRegistrationService;
import com.hoanghiep.hust.service.IUserService;
import com.hoanghiep.hust.utility.VerifierUtils;

@Controller
@RequestMapping("/user")
@Slf4j
public class RegistrationController {

	@Autowired
	private IRegistrationService registrationService;

	@Autowired
	private IUserService userService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping(value = "/registration")
	@PreAuthorize("permitAll")
	public String showRegistrationForm(@ModelAttribute User user) {
		return "registration";
	}

	@PostMapping(value = "/registration")
	@PreAuthorize("permitAll")
	public ModelAndView signUp(@ModelAttribute @Valid User user, BindingResult result) {
		User newUser;
		ModelAndView mav = new ModelAndView();

		try {
			VerifierUtils.verifyModelResult(result);
			newUser = registrationService.startRegistration(user);
		} catch (ModelVerificationException e) {
			mav.setViewName("registration");
			return mav;
		} catch (UserAlreadyExistsException e) {
			if (e.getMessage().contains("username"))
				result.rejectValue("username", "label.user.usernameTaken");
			if (e.getMessage().contains("email"))
				result.rejectValue("email", "label.user.emailInUse");
			mav.setViewName("registration");
			return mav;
		}

		return registrationStepView(newUser, mav);
	}

	@RequestMapping(value = "/{user_id}/continueRegistration", method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ModelAndView nextRegistrationStep(@PathVariable Long user_id, String token) {
		User user = userService.find(user_id);
		registrationService.continueRegistration(user, token);

		ModelAndView mav = new ModelAndView();
		return registrationStepView(user, mav);
	}

	private ModelAndView registrationStepView(User user, ModelAndView mav) {

		if (!registrationService.isRegistrationCompleted(user)) {
			log.info("User {} haven't completed registration", user.getUsername());
			mav.addObject("header", messageSource.getMessage("label.registration.step1.header", null, null));
			mav.addObject("subheader", messageSource.getMessage("label.registration.step1.subheader", null, null));
			mav.setViewName("simplemessage");
		} else {
			log.info("User {} registered successfully", user.getUsername());
			mav.addObject("header", messageSource.getMessage("label.registration.step2.header", null, null));
			mav.addObject("subheader", messageSource.getMessage("label.registration.step2.subheader", null, null));
			mav.setViewName("simplemessage");
		}

		return mav;
	}
}
