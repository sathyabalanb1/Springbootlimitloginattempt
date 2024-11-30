package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;

public interface UserService {
	
	public void saveUser(UserDto userDto);
	
	public User getByEmail(String email);
	
	public void increaseFailedAttempts(User user);
	
	public void resetFailedAttempts(String email);
	
	public void lock(User user);
	
	public boolean unlockWhenTimeExpired(User user);

	public int getMaximumFailedAttempts();
	
	public long getRemainingTime(User user);
	
	public long convertMillisToMinutesAndSeconds (long millis);
	
	public boolean isLockTimeExpired(User user);
	
	public void updateNewPassword(int id, String password);

}
