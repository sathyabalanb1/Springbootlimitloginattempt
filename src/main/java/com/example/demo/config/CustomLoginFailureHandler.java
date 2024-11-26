package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler{
	
//	@Autowired
//    private UserServices userService;
	
	private UserService userService;
	
	public CustomLoginFailureHandler(UserService userService) {
		
		this.userService = userService;
	}
     
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        User user = userService.getByEmail(email);
         
        if (user != null) {
            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < userService.getMaximumFailedAttempts() - 1) {
                    userService.increaseFailedAttempts(user);
                    response.sendRedirect("/invalid?badcredential=invalid");
                } else {
                    userService.lock(user);
                    response.sendRedirect("/invalid?lockederror=locked");
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 24 hours.");
                }
            }else if(!user.isAccountNonLocked()){
            	
            	if(user.getFailedAttempt() == userService.getMaximumFailedAttempts()-1) {
            		response.sendRedirect("/invalid?lockederror=locked");
            	}
            	
            } else if (!user.isAccountNonLocked()) {
                if (userService.unlockWhenTimeExpired(user)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                }
             
        }
         
   //     super.setDefaultFailureUrl("/login?error");
   //     super.onAuthenticationFailure(request, response, exception);
    }else {
    	
    	response.sendRedirect("/invalid?badcredential=invalid");
    	

    }
 

}
    
}
