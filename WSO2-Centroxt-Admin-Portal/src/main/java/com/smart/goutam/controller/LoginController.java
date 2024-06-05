package com.smart.goutam.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.goutam.entity.User;
import com.smart.goutam.service.UserService;

@Controller
public class LoginController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Return the login form template
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login"; // Redirect to the login page
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String email, @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes, Model model) {
        // Find user by email
        User user = userService.findByEmail(email);

        // Check if user exists and password matches
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Check if the user is an admin
            if (user.getRole() == User.Role.ADMIN) {
                // Encode user image to Base64
                String userImage = Base64.getEncoder().encodeToString(user.getImage());

                // Add user-related attributes to the model
                model.addAttribute("user", user);
                model.addAttribute("userImage", userImage);
                model.addAttribute("email", email); // Add email to model
                model.addAttribute("username", user.getUserName()); // Add username to model
                model.addAttribute("firstLetter", user.getUserName().substring(0, 1).toUpperCase()); // Add first letter of userName to model
                model.addAttribute("isAdmin", true); // Mark user as admin

                return "admindashboard"; // Redirect to admin dashboard
            } else {
                // If the user is not an admin, reject the login attempt
                redirectAttributes.addFlashAttribute("error", "Access denied. Only admins can log in.");
                return "redirect:/login";
            }
        } else {
            // Invalid email or password, redirect to login page
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/admindashboard")
    public String showAdminDashboard(Model model) {
        // Get authentication details
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // Fetch logged-in email

        // Find user by email
        User userByEmail = userService.findByEmail(email);

        // Add user-related attributes to the model
        model.addAttribute("email", email);
        model.addAttribute("username", userByEmail.getUserName()); // Add username to model
        model.addAttribute("firstLetter", userByEmail.getUserName().substring(0, 1).toUpperCase()); // Add first letter of userName to model

        return "admindashboard"; // Return admin dashboard view
    }
}
