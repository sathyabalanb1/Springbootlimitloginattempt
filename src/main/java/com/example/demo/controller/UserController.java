package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.UserDto;
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
	public String showLoginForm(Model model) {
				
		return "loginform";
	}
	
	@GetMapping("/homepage")
	public String showHomePage(Model model) {
				
		return "homepage";
	}

}
