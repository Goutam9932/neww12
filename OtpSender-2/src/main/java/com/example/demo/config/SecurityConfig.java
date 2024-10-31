package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.OtpService;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository; // Autowire UserRepository to get User entity directly

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .requestMatchers("/login", "/register", "/otp", "/otp/send").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/user/**", "/user1/**").hasAnyRole("USER", "ADMIN") // Update to cover all user paths
            .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .failureHandler((request, response, exception) -> {
                    response.sendRedirect("/login?error=true");
                })
                .successHandler((request, response, authentication) -> {
                    String username = authentication.getName();
                    User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    
                    // Ensure OTP verification is done and redirect accordingly
                    if (!otpService.isOtpVerified(username)) {
                        otpService.sendOtp(user.getEmail(), false);
                        response.sendRedirect("/otp");
                    } else {
                        if (authentication.getAuthorities().stream()
                                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                            response.sendRedirect("/admin/dashboard");
                        } else {
                            response.sendRedirect("/user/dashboard");
                        }
                    }
                })
                .permitAll()
            .and()
            .logout().permitAll();
        return http.build();
    }


}
