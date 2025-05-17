package com.example.demo.controller;

import com.example.demo.model.UserDetails;
import com.example.demo.repo.UserDetailsRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({ "/admin", "/user1" }) //  This will map both "/admin" and "/user"
public class AdminController {

	private final UserDetailsRepository userRepository;

	public AdminController(UserDetailsRepository userRepository) {
		this.userRepository = userRepository;
	}

	 @GetMapping("/dash")
	    public String adminDashboard(Authentication authentication, Model model) {
	        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser)) {
	            System.out.println("Unauthorized access attempt to /admin/dash");
	            return "error"; // Ensure you have an "error.html" template
	        }

	        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
	        String username = oidcUser.getFullName(); // Fetch full name from OIDC

	        System.out.println(" Admin Dashboard Accessed by: " + username);
	        model.addAttribute("username", username);

	        return "admin-dashboard"; // Ensure this Thymeleaf template exists
	    }

	 @GetMapping("/dash1")
	 public String getAdminDashboard1(Authentication authentication, Model model, HttpSession session) {
	     // Extract OIDC user from authentication
	     OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
	     
	     // Extract username from OIDC claims
	     String username = oidcUser.getClaimAsString("username");
	     if (username == null || username.isEmpty()) {
	         username = oidcUser.getEmail(); // Fallback to email if username is missing
	     }

	     // Store username in session and model
	     session.setAttribute("username", username);
	     model.addAttribute("username", username);

	     // Extract role from OIDC claims (check both 'user_role' and 'usertype')
	     String role = oidcUser.getClaimAsString("user_role"); // Check 'user_role' first
	     if (role == null || role.isEmpty()) {
	         role = oidcUser.getClaimAsString("usertype"); // Fallback to 'usertype'
	     }
	     if (role == null || role.isEmpty()) {
	         role = "USER"; // Default role
	     }

	     // Ensure only admins can access this page
	     if (!"ADMIN".equalsIgnoreCase(role)) {
	         return "error"; // Redirect non-admin users to an error page
	     }

	     // Retrieve all users from the database (you might want to filter or paginate this in real apps)
	     List<UserDetails> allUsers = userRepository.findAll();
	     model.addAttribute("allUsers", allUsers);

	     return "Admindash"; // Return the admin dashboard page
	 }


	@GetMapping("/dash2")
	public String getUserDetails(Authentication authentication, Model model) {
		OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
		String email = oidcUser.getEmail();

		// Fetch user details from the database
		Optional<UserDetails> userOpt = userRepository.findByEmail(email);

		// If user exists, add to model and return dashboard page
		if (userOpt.isPresent()) {
			model.addAttribute("user", userOpt.get());
			return "dashboard"; // ✅ Returns HTML page instead of JSON
		} else {
			return "error"; // Redirect to an error page if user not found
		}

	}

	@GetMapping("/dash3")
	public String getUserDetails3(Authentication authentication, Model model) {
		OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
		String email = oidcUser.getEmail();

		// Fetch user details from the database
		Optional<UserDetails> userOpt = userRepository.findByEmail(email);

		// If user exists, add to model and return dashboard page
		if (userOpt.isPresent()) {
			model.addAttribute("user", userOpt.get());
			return "Userdetailsdash"; // ✅ Returns HTML page instead of JSON
		} else {
			return "error"; // Redirect to an error page if user not found
		}

	}

}
