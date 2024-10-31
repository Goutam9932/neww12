package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.OtpService;

@Controller
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ModelAndView showOtpPage(Authentication authentication) {
        otpService.resetResendAttempts(authentication.getName());
        return new ModelAndView("otp");
    }
    @PostMapping
    public String verifyOtp(@RequestParam String otp, Authentication authentication) {
        String email = userRepository.findByUsername(authentication.getName())
                    .map(User::getEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (otpService.verifyOtp(email, otp)) {
            otpService.resetResendAttempts(email);

            // Redirect based on roles after successful OTP verification
            if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/dashboard";
            }
        }
        return "redirect:/otp?error=true";
    }

    @PostMapping("/send")
    public ResponseEntity<String> resendOtp(Authentication authentication) {
        String email = userRepository.findByUsername(authentication.getName())
                    .map(User::getEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean otpSent = otpService.sendOtp(email, true);
        if (otpSent) {
            return ResponseEntity.ok("OTP resent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                 .body("OTP limit reached");
        }
    }
}
