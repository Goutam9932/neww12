package com.example.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        String targetUrl = "/user/dash"; // Default redirect

        // Check for ROLE_ADMIN
        for (GrantedAuthority authority : authorities) {
            System.out.println(" Checking Role: " + authority.getAuthority());
            if (authority.getAuthority().equals("ROLE_ADMIN")) { 
                targetUrl = "/admin/dash"; 
                break;
            }
        }

        // Debugging logs
        System.out.println(" User Roles: " + authorities);
        System.out.println("Redirecting to: " + targetUrl);

        // Redirect to the determined target URL
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
