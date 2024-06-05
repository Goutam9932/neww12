package com.smart.goutam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.goutam.entity.Group;
import com.smart.goutam.entity.User;
import com.smart.goutam.service.GroupService;
import com.smart.goutam.service.UserService;

@Controller
public class EditDeleteGroupController {

    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public EditDeleteGroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @GetMapping("/edit-group/{id}")
    public String editGroup(@PathVariable("id") Long id, Model model) {
        Group group = groupService.getGroupById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        // Find user by email
        User userByEmail = userService.findByEmail(email);
        
        // Fetch all groups
        List<Group> groups = groupService.getAllGroups();
        
        // Add user-related attributes to the model for the logged-in user
        model.addAttribute("email", email);
        model.addAttribute("username", userByEmail.getUserName());
        model.addAttribute("firstLetter", userByEmail.getUserName().substring(0, 1).toUpperCase());
        model.addAttribute("group", group);
        return "editgroup"; // Name of the Thymeleaf template for editing the group
    }
    @PostMapping("/update-group/{id}")
    @ResponseBody
    public ResponseEntity<String> updateGroup(@PathVariable("id") Long id, @RequestParam("groupName") String groupName) {
        Group group = groupService.getGroupById(id);
        group.setGroupName(groupName);
        groupService.saveGroup(group);
        return ResponseEntity.ok("Group updated successfully");
    }
   
}
