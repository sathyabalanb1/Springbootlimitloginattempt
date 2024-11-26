package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private UserDetailsService userDetailsService;
	
	private CustomLoginSuccessHandler loginSuccessHandler;
	
	private CustomLoginFailureHandler loginFailureHandler;
	
	public SecurityConfig(UserDetailsService userDetailsService,CustomLoginSuccessHandler loginSuccessHandler, CustomLoginFailureHandler loginFailureHandler ) {
		
		this.userDetailsService = userDetailsService;
		this.loginSuccessHandler = loginSuccessHandler;
		this.loginFailureHandler = loginFailureHandler;
	}
	
	@Bean
	public static PasswordEncoder passwordEncoder() {

		return NoOpPasswordEncoder.getInstance();
	}
	
	
	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider daoAuth = new DaoAuthenticationProvider();
		daoAuth.setUserDetailsService(userDetailsService);
		daoAuth.setPasswordEncoder(passwordEncoder());
		
		return daoAuth;
	}
	
		
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf().disable()
		.authorizeHttpRequests((authorize) ->
		authorize.requestMatchers(new AntPathRequestMatcher("/registerform")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/register/save")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/homepage")).hasAnyRole("ADMIN", "USER")
		.requestMatchers(new AntPathRequestMatcher("/invalid/**")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/login/**")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/forgotpassword/**")).permitAll()
		)
		.formLogin( form  -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.failureHandler(loginFailureHandler)
				.successHandler(loginSuccessHandler)
				.permitAll()
				).logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.permitAll()
						);
		
		return http.build();
				
		
	}
	
	/*
	public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
		
		builder.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder());
		
	}
	*/

}
