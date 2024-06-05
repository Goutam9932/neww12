
package com.smart.goutam.service;

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
    
    private final GroupService groupService; // Autowire GroupService

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, GroupService groupService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService; // Initialize GroupService
    }

    public ResponseEntity<User> createUser(User user) {
        Group group = groupService.getGroupById(user.getGroup().getId()); // Get group by ID from user object
        if (group == null) {
            // Handle case where group is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        user.setGroup(group);
        
        // Encode the user's password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User createdUser = userRepository.save(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

}
