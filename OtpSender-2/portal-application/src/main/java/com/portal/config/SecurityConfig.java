package com.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            InMemoryClientRegistrationRepository clientRepo) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                .dispatcherTypeMatchers(
                        jakarta.servlet.DispatcherType.FORWARD,
                        jakarta.servlet.DispatcherType.ERROR
                ).permitAll()

                .requestMatchers(
                        "/login",
                        "/register",
                        "/logout",
                        "/logout-success",
                        "/error",
                        "/css/**",
                        "/js/**",
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/sso",
                        "/api/auth/me",
                        "/oauth2/**",
                        "/login/**"
                ).permitAll()

                .anyRequest().authenticated()
            )

            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    .defaultSuccessUrl("/dashboard", true)
                    .failureUrl("/login?error")
            )

            .exceptionHandling(exception -> exception

                    .defaultAuthenticationEntryPointFor(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                            new AntPathRequestMatcher("/api/**")
                    )

                    .authenticationEntryPoint(
                            new LoginUrlAuthenticationEntryPoint("/login")
                    )
            )

            .logout(logout -> logout

                    .logoutUrl("/api/auth/logout")

                    .logoutSuccessHandler((request, response, authentication) -> {

                        // ===== DEBUG LOGS =====
                        System.out.println("=== LOGOUT DEBUG ===");
                        System.out.println("Authentication: " + authentication);

                        System.out.println("Authentication type: " +
                                (authentication != null
                                        ? authentication.getClass().getName()
                                        : "NULL"));

                        // Capture ID Token
                        String idToken = null;

                        if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauthToken
                                && oauthToken.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {

                            idToken = oidcUser.getIdToken().getTokenValue();

                            System.out.println(
                                    "ID Token found: " +
                                            idToken.substring(0, 20) + "..."
                            );

                        } else {
                            System.out.println("ID Token NOT found - not SSO login");
                        }

                        // Invalidate Session
                        jakarta.servlet.http.HttpSession session =
                                request.getSession(false);

                        if (session != null) {
                            session.invalidate();
                        }

                        // Redirect Logic
                        if (idToken != null) {

                            String encodedToken =
                                    java.net.URLEncoder.encode(
                                            idToken,
                                            java.nio.charset.StandardCharsets.UTF_8
                                    );

                            String encodedRedirect =
                                    java.net.URLEncoder.encode(
                                            "http://localhost:8080/login",
                                            java.nio.charset.StandardCharsets.UTF_8
                                    );

                            String redirectUrl =
                                    "https://wso2.cnxy.in/oidc/logout"
                                            + "?id_token_hint=" + encodedToken
                                            + "&post_logout_redirect_uri="
                                            + encodedRedirect;

                            System.out.println(
                                    "Redirecting to WSO2: " + redirectUrl
                            );

                            response.sendRedirect(redirectUrl);

                        } else {

                            System.out.println(
                                    "Redirecting to local login"
                            );

                            response.sendRedirect("/login?logout");
                        }
                    })

                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}