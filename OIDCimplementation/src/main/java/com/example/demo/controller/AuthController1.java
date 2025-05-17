package com.example.demo.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController1 {

    @Value("${client.logouturi.gluu}")
    private String gluuLogoutUri;

    @Value("${client.post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    @Value("${client.logouturi.wso2}")
    private String wso2LogoutUri;

    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    org.slf4j.Logger logger = LoggerFactory.getLogger(AuthController1.class);

    @GetMapping("/endsession")
    public String endSession(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Logout request initiated");

        // Perform Spring Security logout
        this.logoutHandler.logout(request, response, authentication);

        if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
            String issuer = oidcUser.getIssuer().toString();

            String logoutRedirectUrl;
            if (issuer.contains("sso.cnxy.in")) {
                // User authenticated via Gluu
                logoutRedirectUrl = gluuLogoutUri + "?post_logout_redirect_uri=" + postLogoutRedirectUri;
                logger.info("Logging out from Gluu");
            } else if (issuer.contains("wso2.cnxy.in")) {
                // User authenticated via WSO2
                logoutRedirectUrl = wso2LogoutUri + "?post_logout_redirect_uri=" + postLogoutRedirectUri;
                logger.info("Logging out from WSO2");
            } else {
                // Default logout redirect if provider is unknown
                logoutRedirectUrl = "/login?logout";
                logger.info("Unknown provider, redirecting to login page.");
            }

            return "redirect:" + logoutRedirectUrl;
        }

        // Fallback if no authentication found
        return "redirect:/login?logout";
    }
}

