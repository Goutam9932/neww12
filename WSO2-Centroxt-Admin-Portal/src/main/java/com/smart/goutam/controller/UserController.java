package com.smart.goutam.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smart.goutam.entity.Group;
import com.smart.goutam.entity.User;
import com.smart.goutam.repo.UserRepository;
import com.smart.goutam.service.GroupService;
import com.smart.goutam.service.PasswordService;
import com.smart.goutam.service.UserService;

@Controller
public class UserController {

    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute User user,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "error"; // Handle errors appropriately
        }

        try {
            if (!imageFile.isEmpty()) {
                user.setImage(imageFile.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.rejectValue("image", "error.user", "Could not process image file");
            return "error";
        }

        // Add code to save the user object
        userService.createUser(user);

        model.addAttribute("message", "User successfully registered");
        return "success";
    }

    @GetMapping("/adduser")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        List<Group> groups = groupService.getAllGroups();
        model.addAttribute("groups", groups);
        return "UserRegistration"; // Assuming "UserRegistration" is the correct HTML file name
    }

    @GetMapping("/userdtls")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // Fetch logged-in email

        // Find user by email
        User userByEmail = userService.findByEmail(email);
        // Add user-related attributes to the model
        model.addAttribute("email", email);
        model.addAttribute("username", userByEmail.getUserName()); // Add username to model
        model.addAttribute("firstLetter", userByEmail.getUserName().substring(0, 1).toUpperCase()); 
        model.addAttribute("users", users);
        return "User"; // Assuming "UserRegistration" is the correct HTML file name
    }

    @GetMapping("/generate")
    @ResponseBody
    public String generatePassword() {
        return passwordService.generatePassword();
    }
}
