package com.smart.goutam.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.goutam.entity.Group;
import com.smart.goutam.entity.User;
import com.smart.goutam.service.UserService;

@Controller
public class EditUserController {

    private final UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public EditUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/edit/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        // Fetch user by ID
        User userById = userService.findById(id);
        
        // Fetch logged-in user's email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        // Find user by email
        User userByEmail = userService.findByEmail(email);
        
        // Fetch all groups
        List<Group> groups = userService.getAllGroups();
        
        // Add user-related attributes to the model for the logged-in user
        model.addAttribute("email", email);
        model.addAttribute("username", userByEmail.getUserName());
        model.addAttribute("firstLetter", userByEmail.getUserName().substring(0, 1).toUpperCase());
        
        // Add groups to the model
        model.addAttribute("groups", groups);
        
        // Check if userById is found and add to the model
        if (userById != null) {
            model.addAttribute("user", userById);
            return "editUser"; // Ensure this template name matches the actual file name
        } else {
            return "redirect:/error"; // Handle the case when user is not found
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user");
        }
    }
    
    
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes redirectAttributes,
                             Model model) 
    {
        try {
            // Check if image file is provided
            if (!imageFile.isEmpty()) {
                // If provided, set the image data
                user.setImage(imageFile.getBytes());
            } else {
                // If not provided, retain the existing image data
                User existingUser = userService.findById(id);
                user.setImage(existingUser.getImage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Fetch and set the group if it's provided
        if (user.getGroup() != null && user.getGroup().getId() != null) {
            Group existingGroup = userService.getGroupById(user.getGroup().getId());
            if (existingGroup != null) {
                user.setGroup(existingGroup);
            }
        }

        // Encode password if it's changed
        User existingUser = userService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        // Find user by email
        User userByEmail = userService.findByEmail(email);
        
        // Fetch all groups
        List<Group> groups = userService.getAllGroups();
        
        // Add user-related attributes to the model for the logged-in user
        model.addAttribute("email", email);
        model.addAttribute("username", userByEmail.getUserName());
        model.addAttribute("firstLetter", userByEmail.getUserName().substring(0, 1).toUpperCase());
        if (!user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }

        // Save the updated user details
        userService.save(user);

        // Add success message to the model
        model.addAttribute("message", "User updated successfully");

        // Return the view name of the same page, assuming it's named 'editUser'
        return "editUser"; // Make sure this matches the name of your Thymeleaf template
    }

}
