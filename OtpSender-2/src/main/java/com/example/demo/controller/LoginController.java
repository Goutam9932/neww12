package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;

@Controller
public class LoginController {

	@Autowired
    private UserRepository userRepository;
	
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login"; // Return the login HTML page
    }
   


    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin_dashboard"; // Return the dashboard HTML page after login
    }
    @GetMapping("/user/dashboard1")
    public String dashboard1(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("User is not authenticated!");
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        model.addAttribute("user", user);
        return "userdash"; // Correct Thymeleaf template
    }

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "userdetails1";  // Correct Thymeleaf template for dashboard view
    }

}


