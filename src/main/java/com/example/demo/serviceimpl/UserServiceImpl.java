package com.example.demo.serviceimpl;

import java.util.Arrays;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{
	
	public static final int MAX_FAILED_ATTEMPTS = 3;
	
	private static final long LOCK_TIME_DURATION = 30 * 60 * 1000;

	
	private UserRepository userRepository;
	
	private RoleRepository roleRepository;
	
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
		
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public void saveUser(UserDto userDto) {
		// TODO Auto-generated method stub
		
		User user = new User();
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		Role role = roleRepository.findByName("ROLE_USER");
		
		user.setRoles(Arrays.asList(role));
		userRepository.save(user);
		
		
	}
	
	@Override
	@Transactional
	public void increaseFailedAttempts(User user) {
		int newFailAttempts = user.getFailedAttempt() + 1;
		String email = user.getEmail();
		userRepository.updateFailedAttempts(newFailAttempts, user.getEmail());
	}
	
	@Override
	public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(0, email);
    }
	
	@Override
	public void lock(User user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());

		userRepository.save(user);
	}

	@Override
	public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
         
        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
             
            userRepository.save(user);
             
            return true;
        }
         
        return false;
    }
	
	@Override
	public User getByEmail(String email) {
		
		User user = userRepository.findByEmail(email);
		
		return user;
	}
	
	@Override
	public int getMaximumFailedAttempts() {
		
		return MAX_FAILED_ATTEMPTS;
	}
	
	@Override
	public long getRemainingTime(User user) {
		long lockTimeInMillis = user.getLockTime().getTime();
		long currentTimeInMillis = System.currentTimeMillis();

		long remainingminutes;

		long lockPeriodInMillis = currentTimeInMillis - lockTimeInMillis;

		if(lockPeriodInMillis > LOCK_TIME_DURATION)
		{
			remainingminutes = 0; 
		}
		else
		{
			long remainingTimeInMillis = LOCK_TIME_DURATION - lockPeriodInMillis;
			remainingminutes = convertMillisToMinutesAndSeconds(remainingTimeInMillis);
		}

		return remainingminutes;

	}
	
	@Override
	public long convertMillisToMinutesAndSeconds (long millis)
	{
		long totalSeconds = millis / 1000;
		long minutes = totalSeconds / 60;
		long seconds = totalSeconds % 60;
	
		return minutes;
	}
	
	@Override
	public boolean isLockTimeExpired(User user)
	{
		long lockTimeInMillis = user.getLockTime().getTime();
		long currentTimeInMillis = System.currentTimeMillis();

		if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
			return true;
		}

		return false;
	}
	
	@Override
	public void updateNewPassword(int id, String password) {
		
		User user = userRepository.findById(id).orElse(null);
		
		user.setPassword(password);
		
		userRepository.save(user);
		
		return;
	}

}
