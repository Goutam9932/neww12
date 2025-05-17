//package com.example.demo.controller;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Controller
//public class AuthController {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
//
//    @Value("${client.logout-url}")
//    private String clientLogoutUrl;
//
//    @Value("${client.post-logout-redirect-uri}")
//    private String postLogoutRedirectUri;
//
//    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
//
//    /** ✅ Redirect dynamically based on the IDP selected **/
//    @GetMapping("/login")
//    public String loginRedirect(HttpServletRequest request) {
//        String idp = request.getParameter("idp");  // Get the selected IDP (WSO2 or Gluu)
//        logger.info("Redirecting to OIDC login for IDP: " + idp);
//
//        if ("wso2".equalsIgnoreCase(idp)) {
//            return "redirect:/oauth2/authorization/wso2";  // ✅ WSO2 Login
//        } else {
//            return "redirect:/oauth2/authorization/gluu";  // ✅ Gluu Login (default)
//        }
//    }
//
//    /** ✅ Custom Logout Handling **/
//    @GetMapping("/endsession")
//    public String endSession(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
//        logger.info("Logout request initiated for all IDPs");
//
//        logoutHandler.logout(request, response, authentication);
//
//        String idTokenHint = "";
//        String sid = "";
//        if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
//            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
//            idTokenHint = oidcUser.getIdToken().getTokenValue();
//            sid = oidcUser.getClaim("sid");
//        }
//
//        // WSO2 Logout URL
//        String wso2LogoutUrl = "https://wso2.cnxy.in/oidc/logout?id_token_hint=" + idTokenHint + "&sid=" + sid
//                + "&post_logout_redirect_uri=" + postLogoutRedirectUri;
//
//        // Gluu Logout URL
//        String gluuLogoutUrl = "https://sso.cnxy.in/oxauth/restv1/end_session?id_token_hint=" + idTokenHint + "&sid=" + sid
//                + "&post_logout_redirect_uri=" + postLogoutRedirectUri;
//
//        logger.info("Logging out from WSO2: " + wso2LogoutUrl);
//        logger.info("Logging out from Gluu: " + gluuLogoutUrl);
//
//        // Call both logout URLs
//        try {
//            new java.net.URL(wso2LogoutUrl).openStream().close();
//            new java.net.URL(gluuLogoutUrl).openStream().close();
//        } catch (Exception e) {
//            logger.error("Error during IDP logout", e);
//        }
//
//        // Redirect user to post-logout page
//        return "redirect:" + postLogoutRedirectUri;
//    }
//
//}
