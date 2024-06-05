package com.smart.goutam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.goutam.entity.Group;
import com.smart.goutam.entity.User;
import com.smart.goutam.repo.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final GroupService groupService;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
			GroupService groupService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.groupService = groupService;
	}

	// This method retrieves a user by email
	public ResponseEntity<User> createUser(User user) {
		Group group = groupService.getGroupById(user.getGroup().getId());
		if (group == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		user.setGroup(group);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		User createdUser = userRepository.save(user);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	// This method retrieves a user by email
	@Override
	public User findByEmail(String email) {
		return userRepository.findFirstByEmail(email);
	}

	// This method retrieves a user by username

	// This method saves a user
	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
	


	public User findById(Long id) {
	    return userRepository.findById(id).orElse(null);
	}
	public void deleteUserById(Long id) {
	    userRepository.deleteById(id);
	}

	@Override
	public List<Group> getAllGroups() {
	    return groupService.getAllGroups(); // Assuming you have a method in GroupService to retrieve all groups
	}

	@Override
	public Group getGroupById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}



	


}
