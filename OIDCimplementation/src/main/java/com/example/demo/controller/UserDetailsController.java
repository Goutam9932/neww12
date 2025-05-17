package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

//import com.example.demo.model.UserDetails;
//import com.example.demo.repo.UserDetailsRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserDetailsController {



//    
	@GetMapping("/login1")
	public String showLoginPage() {
		// System.out.println("Custom login page displayed");
		return "login"; // Thymeleaf template for login page
	}

}
