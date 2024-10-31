package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final JavaMailSender emailSender;
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, LocalDateTime> otpExpiryTimeStorage = new HashMap<>();
    private final Map<String, Integer> resendAttempts = new HashMap<>();
    private final Map<String, Boolean> verifiedStatus = new ConcurrentHashMap<>(); // New map for verification status
    private static final int EXPIRATION_MINUTES = 2;
    private static final int MAX_RESEND_ATTEMPTS = 3;

    @Autowired
    public OtpService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public boolean sendOtp(String email, boolean isResend) {
        if (isResend) {
            int attempts = resendAttempts.getOrDefault(email, 0);
            if (attempts >= MAX_RESEND_ATTEMPTS) {
                return false;
            }
            resendAttempts.put(email, attempts + 1);
        }

        String otp = generateOtp();
        otpStorage.put(email, otp);
        otpExpiryTimeStorage.put(email, LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));

        sendEmail(email, otp);
        return true;
    }

    public void resetResendAttempts(String email) {
        resendAttempts.put(email, 0);
    }

    public void resetOtp(String email) {
        otpStorage.remove(email);
        otpExpiryTimeStorage.remove(email);
        resendAttempts.remove(email);
        verifiedStatus.remove(email); // Clear verification status on OTP reset
    }

    public boolean verifyOtp(String email, String otp) {
        if (otpStorage.containsKey(email)) {
            LocalDateTime expirationTime = otpExpiryTimeStorage.get(email);

            if (LocalDateTime.now().isBefore(expirationTime)) {
                if (otp.equals(otpStorage.get(email))) {
                    resetOtp(email); // Clear OTP on successful verification
                    setOtpVerified(email); // Mark OTP as verified
                    return true;
                }
            } else {
                resetOtp(email); // Clear expired OTP
            }
        }
        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private void sendEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + ". It is valid for 2 minutes.");
        emailSender.send(message);
    }

    // New method to set OTP verification status
    public void setOtpVerified(String email) {
        verifiedStatus.put(email, true);
    }

    // New method to check OTP verification status
    public boolean isOtpVerified(String email) {
        return verifiedStatus.getOrDefault(email, false);
    }

    public void sendRegistrationEmail(String email, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Welcome to Our Service - Your Registration Details");
        message.setText("Dear User,\n\nYour registration was successful! Here are your login details:\n\n" +
                        "Username: " + username + "\n" +
                        "Password: " + password + "\n\n" +
                        "Please keep this information secure.\n\n" +
                        "Best regards,\nYour Company Team");
        emailSender.send(message);
    }
}
