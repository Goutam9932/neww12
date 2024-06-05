package com.smart.goutam.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.smart.goutam.entity.Group;
import com.smart.goutam.entity.User;

public interface UserService {

	ResponseEntity<User> createUser(User user);

	User findByEmail(String email);

	void save(User user);

	User findById(Long id);

	void deleteUserById(Long id);

	List<Group> getAllGroups();

	Group getGroupById(Long id);

	

	//User findByUsername(String userName);

	

	
	
}
