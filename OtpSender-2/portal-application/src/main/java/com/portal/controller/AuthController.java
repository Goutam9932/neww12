package com.portal.controller;

import com.portal.entity.Role;
import com.portal.entity.User;
import com.portal.repository.RoleRepository;
import com.portal.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    // Login DTO
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // Register DTO
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String department;
        private String fullName;
        private String role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    // Response DTO
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String department;
        private String fullName;
        private List<String> roles;

        public UserResponse(String username, String email, String department, String fullName, List<String> roles) {
            this.username = username;
            this.email = email;
            this.department = department;
            this.fullName = fullName;
            this.roles = roles;
        }

        public UserResponse(Long id, String username, String email, String department, String fullName, List<String> roles) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.department = department;
            this.fullName = fullName;
            this.roles = roles;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getFullName() { return fullName; }
        public List<String> getRoles() { return roles; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not found"));
            }

            List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

            return ResponseEntity.ok(new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getDepartment(),
                user.getFullName(),
                roles
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Username is already taken"));
        }

        User newUser = new User(
            registerRequest.getUsername(),
            passwordEncoder.encode(registerRequest.getPassword()),
            registerRequest.getEmail(),
            registerRequest.getDepartment(),
            registerRequest.getFullName()
        );

        // Retrieve and assign requested role, fallback to ROLE_HR if not provided or invalid
        String requestedRole = registerRequest.getRole();
        if (requestedRole == null || requestedRole.trim().isEmpty()) {
            requestedRole = "ROLE_HR";
        }
        
        // Basic security validation for requested role
        if (!Arrays.asList("ROLE_ADMIN", "ROLE_HR", "ROLE_FINANCE", "ROLE_IT").contains(requestedRole)) {
            requestedRole = "ROLE_HR";
        }

        Role userRole = roleRepository.findByName(requestedRole).orElse(null);
        if (userRole != null) {
            newUser.setRoles(new HashSet<>(Arrays.asList(userRole)));
        }

        userRepository.save(newUser);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/sso")
    public ResponseEntity<?> ssoLogin(HttpServletRequest request, HttpServletResponse response) {
        // Mock SSO logic: Retrieve or create a special user "sso_user" mapping to WSO2
        String ssoUsername = "sso_user";
        User user = userRepository.findByUsername(ssoUsername).orElse(null);
        
        if (user == null) {
            user = new User(
                ssoUsername,
                passwordEncoder.encode(UUID.randomUUID().toString()), // Random password
                "sso.user@company.com",
                "Executive Office",
                "SSO Corporate User"
            );
            
            // Assign ROLE_ADMIN and ROLE_HR for full visual demo capabilities
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
            Role hrRole = roleRepository.findByName("ROLE_HR").orElse(null);
            Set<Role> roles = new HashSet<>();
            if (adminRole != null) roles.add(adminRole);
            if (hrRole != null) roles.add(hrRole);
            user.setRoles(roles);
            
            userRepository.save(user);
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> (GrantedAuthority) role::getName)
            .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            null,
            authorities
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new UserResponse(
            user.getUsername(),
            user.getEmail(),
            user.getDepartment(),
            user.getFullName(),
            roles
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            // Handle mock/in-memory principals if any
            List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
            return ResponseEntity.ok(new UserResponse(username, "", "Remote SSO", username, roles));
        }

        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new UserResponse(
            user.getUsername(),
            user.getEmail(),
            user.getDepartment(),
            user.getFullName(),
            roles
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }

        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied"));
        }

        List<User> users = userRepository.findAll();
        List<UserResponse> response = users.stream()
            .map(user -> {
                List<String> userRoles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
                return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getDepartment(),
                    user.getFullName(),
                    userRoles
                );
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
