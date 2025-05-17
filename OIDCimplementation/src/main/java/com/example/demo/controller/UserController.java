package com.example.demo.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.UserDetails;
import com.example.demo.repo.UserDetailsRepository;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserDetailsRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	public UserController(UserDetailsRepository userRepository) {
		this.userRepository = userRepository;

	}

	@GetMapping("/dash")
	public String getUserDetails(Authentication authentication, Model model) {
		OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
		String email = oidcUser.getEmail();

		// Fetch user details from the database
		Optional<UserDetails> userOpt = userRepository.findByEmail(email);

		if (userOpt.isPresent()) {
			UserDetails user = userOpt.get();

			// Extract username (fallback to email if missing)
			String username = user.getUsername() != null ? user.getUsername() : email;

			// Add user and username to the model
			model.addAttribute("user", user);
			model.addAttribute("username", username);

			return "dashboard1"; // ✅ Render dashboard1.html
		} else {
			return "error"; // Redirect to error page if user not found
		}
	}

}