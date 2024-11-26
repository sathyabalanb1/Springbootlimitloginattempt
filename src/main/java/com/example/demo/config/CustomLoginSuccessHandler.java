package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	private UserService userService;
	
	public CustomLoginSuccessHandler(UserService userService) {
		
		this.userService = userService;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	            Authentication authentication) throws IOException, ServletException {
		
			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			User user = userService.getByEmail(email);
	        
	        if(user.getLockTime() != null && !user.isAccountNonLocked()) {
	        	
	        		if(!userService.isLockTimeExpired(user)) {
	        			
	        			long minutes = userService.getRemainingTime(user);
	    				
	    				response.sendRedirect("/login?locktimecredentials="+minutes);
	    				return;
	    				
	        		}else if(userService.isLockTimeExpired(user)) {
	        			
	        			response.sendRedirect("/forgotpassword/form");
	        			return;
	        		}
	        		else if(user.getFailedAttempt() >0) {
	        			
	        			userService.unlockWhenTimeExpired(user);
	        			
	        		//	userService.resetFailedAttempts(user.getEmail());
	        		}
	        	}
	        
	        response.sendRedirect("/homepage");
	         
	   //     super.onAuthenticationSuccess(request, response, authentication);
	}

}
