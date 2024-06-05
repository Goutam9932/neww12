package com.smart.goutam.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.smart.goutam.entity.User;
import com.smart.goutam.repo.UserRepository;


@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	
	private UserRepository userRepository;  
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    try {
	        User user = userRepository.getUserByName(username);
	        if (user == null) {
	            throw new UsernameNotFoundException("User not found with email: " + username);
	        }
	        return new CustomUserDetails(user);
	    } catch (Exception e) {
	        // Log the error for debugging purposes
	        e.printStackTrace();
	        throw new UsernameNotFoundException("Error occurred while loading user by email: " + e.getMessage());
	    }
	}


}