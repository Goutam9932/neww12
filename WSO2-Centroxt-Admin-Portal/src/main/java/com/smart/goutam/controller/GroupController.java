package com.smart.goutam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.goutam.entity.Group;
import com.smart.goutam.entity.User;
import com.smart.goutam.service.GroupService;
import com.smart.goutam.service.UserService;

@Controller
public class GroupController {

	 private final GroupService groupService;
	    private final UserService userService;

	    @Autowired
	    public GroupController(GroupService groupService, UserService userService) {
	        this.groupService = groupService;
	        this.userService = userService;
	    }

	    @GetMapping("/newGroupName")
	    public String showGroup(Model model) {
	        model.addAttribute("user", new User());
	        return "Group";
	    }

	    @PostMapping("/newGroup")
	    public String addGroup(@RequestParam("groupName") String groupName, Model model) {
	        Group group = new Group();
	        group.setGroupName(groupName);
	        groupService.createGroup(group);

	        // Fetch all groups again
	        List<Group> groups = groupService.getAllGroups();
	        model.addAttribute("groups", groups);

	        return "redirect:/UserRegistration"; // Redirect to the user registration form
	    }
	    @GetMapping("/GroupUI")
	    public String showGroupUI(Model model) {
	        List<Group> groups = groupService.getAllGroups();
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        String email = auth.getName(); // Fetch logged-in email

	        // Find user by email
	        User userByEmail = userService.findByEmail(email);

	        // Add user-related attributes to the model
	        model.addAttribute("email", email);
	        model.addAttribute("username", userByEmail.getUserName()); // Add username to model
	        model.addAttribute("firstLetter", userByEmail.getUserName().substring(0, 1).toUpperCase());
	        model.addAttribute("groups", groups);

	        return "GroupUI";
	    }
	    
	   
	}