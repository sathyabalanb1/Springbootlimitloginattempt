package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		
		this.userService = userService;
	}
		
	@GetMapping("/registerform")
	public String showRegistrationForm(Model model) {
		
		UserDto user = new UserDto();
		
		model.addAttribute("user", user);
				
		return "userregistrationform";
	}
	
	@PostMapping("/register/save")
	public String register(@Valid @ModelAttribute("user") UserDto user,
				BindingResult result,
				Model model) {
		
		if(result.hasErrors()) {
			model.addAttribute("user", user);
			return "userregistrationform";
		}
		
		userService.saveUser(user);
		
		return "redirect:/registerform?success";
	}
	
	@GetMapping("/login")
	public String showLoginForm(Model model, @RequestParam(name="locktimecredentials", required=false) String locktimecredentials) {
		
		if(locktimecredentials != null) {
			model.addAttribute("locktimecredentials", locktimecredentials);
			return "loginform";
		}
		
		return "loginform";
	}
	
	@GetMapping("/homepage")
	public String showHomePage(Model model) {
				
		return "homepage";
	}
	
	@GetMapping("/invalid")
	public String invalid(@RequestParam(name="badcredential", required=false) String badcredential,@RequestParam(name="locktimeerror", required=false) String locktimeerror,@RequestParam(name="lockederror", required=false) String lockederror,Model model)
	{
		if(locktimeerror != null)
		{
			model.addAttribute("locktimeerror", locktimeerror);
			return "loginform";
		}
		else if(lockederror != null){
			model.addAttribute("lockederror", lockederror);
			return "loginform";
		}
		else
		{
			model.addAttribute("badcredential", badcredential);
			return "loginform";
		}
	}
	
	@GetMapping("/forgotpassword/form")
	public String displayForgotPasswordForm() {
		
		return "forgotpasswordform";
	}
	
	@PostMapping("/forgotpassword/process")
	public String forgotPasswordProcess(@RequestParam("username") String email,Model model)
	{
		User user = userService.getByEmail(email);

		if(user != null)
		{ 
			if(user.getLockTime() != null)
			{
				if(!userService.isLockTimeExpired(user))
				{
					long balanceMinutes = userService.getRemainingTime(user);

					model.addAttribute("balanceminutes", balanceMinutes);
					return "forgotpasswordform";
				}
			}
			model.addAttribute("user", user);
			return "resetpasswordform";
		}
		else {
			model.addAttribute("usernotexist", "User Doesn't Exist");
			return "forgotpasswordform";
		}

	}

	@PostMapping("/forgotpassword/reset")
	public String resetPasswordProcess(@RequestParam("newpassword") String newpassword, @RequestParam("useremail") String email,Model model)
	{
		
		
		User user = userService.getByEmail(email);
		
		String oldpassword = user.getPassword();
		
		boolean validpassword = newpassword.equals(oldpassword);

		if(validpassword == true)
		{
			model.addAttribute("samepassword","New Password should be differ from Old Password");
			model.addAttribute("user",user);
			return "resetpasswordform";
		}		
		
		if(user.getLockTime() != null)
		{
		userService.unlockWhenTimeExpired(user);
		}
		
		userService.updateNewPassword(user.getId(), newpassword);

		return "loginform";


	}


}
