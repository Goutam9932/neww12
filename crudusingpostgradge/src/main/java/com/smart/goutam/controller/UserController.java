package com.smart.goutam.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smart.goutam.entity.Group;
import com.smart.goutam.entity.User;
import com.smart.goutam.service.GroupService;
import com.smart.goutam.service.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {

	 private final UserService userService;
	    private final GroupService groupService;

	    @Autowired
	    public UserController(UserService userService, GroupService groupService) {
	        this.userService = userService;
	        this.groupService = groupService;
	    }

	    @PostMapping("/newuser")
	    public ResponseEntity<User> addUser(@RequestParam("userName") String userName,
	                                        @RequestParam("firstName") String firstName,
	                                        @RequestParam("lastName") String lastName,
	                                        @RequestParam("country") String country,
	                                        @RequestParam("email") String email,
	                                        @RequestParam("mobile") String mobile,
	                                        @RequestParam("password") String password,
	                                        @RequestParam("role") String role,
	                                        @RequestParam("groupId") Long groupId,
	                                        @RequestParam("file") MultipartFile file) {
	        // Handle file upload here
	        if (file.isEmpty()) {
	            // Handle case where file is empty
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        }

	        try {
	            // Create a new User object
	            User user = new User();
	            user.setUserName(userName);
	            user.setFirstName(firstName);
	            user.setLastName(lastName);
	            user.setCountry(country);
	            user.setEmail(email);
	            user.setMobile(mobile);
	            user.setPassword(password);
	            
	            // Set role
	            user.setRole(User.Role.valueOf(role)); // Assuming role is provided as a string
	            
	            // Set group
	            Group group = groupService.getGroupById(groupId); // Assuming you have a method to get group by ID
	            if (group == null) {
	                // Handle case where group is not found
	                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	            }
	            user.setGroup(group);
	            
	            // Set image data
	            user.setImage(file.getBytes());
	            
	            // Call the service method to save the user
	            return userService.createUser(user);
	        } catch (IOException e) {
	            e.printStackTrace();
	            // Handle file upload error
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

    @PostMapping("/newGroup")
    public ResponseEntity<Group> addGroup(@RequestBody Group group) {
        return groupService.createGroup(group);
    }
}