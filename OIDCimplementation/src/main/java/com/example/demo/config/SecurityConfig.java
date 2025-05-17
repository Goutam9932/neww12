package com.example.demo.config;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.demo.model.UserDetails;
import com.example.demo.repo.UserDetailsRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsRepository userRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(UserDetailsRepository userRepository,
                          ClientRegistrationRepository clientRegistrationRepository) {
        this.userRepository = userRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationSuccessHandler successHandler)
            throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login1", "/oauth2/**", "/login", "/SSO.jpg").permitAll()
                .requestMatchers("/user/dash", "/user/update").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers("/admin/dash").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .authorizationEndpoint(authEndpoint ->
                        authEndpoint.authorizationRequestResolver(customAuthorizationRequestResolver()))
                .successHandler(successHandler)
                .failureHandler((request, response, exception) -> {
                    System.err.println("OAuth2 login failed: " + exception.getMessage());
                    response.sendRedirect("/login?oauth_error=true");
                })
        )
        .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll());

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
        return new CustomOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "test_goutam_passport");
    }

    @Bean
    public OidcUserService oidcUserService() {
        OidcUserService delegate = new OidcUserService();

        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
                OidcUser oidcUser = delegate.loadUser(userRequest);

                // Ensure issuerUri is not null before using contains()
                String issuerUri = userRequest.getClientRegistration().getProviderDetails().getIssuerUri();
                boolean isWSO2 = (issuerUri != null) && issuerUri.contains("wso2");

                System.out.println("OIDC Provider: " + issuerUri);
                System.out.println("OIDC Claims: " + oidcUser.getClaims());

                // Extract user info
                String email = oidcUser.getClaimAsString("email");
                String firstName = oidcUser.getClaimAsString("given_name");
                String lastName = oidcUser.getClaimAsString("family_name");
                String sub = oidcUser.getClaimAsString("sub");
                String username = oidcUser.getClaimAsString("username");
                String birthdate = oidcUser.getClaimAsString("birthdate");
	            String userStatus = isWSO2 ? null : oidcUser.getClaimAsString("jans_status");
	            String updatedAt = oidcUser.getClaimAsString("updated_at");
	            String nickname = oidcUser.getClaimAsString("nickname");
                
                // Extract role information
	         // Extract role information
	            String role = oidcUser.getClaimAsString("user_role"); // Check for 'user_role' first
	            if (role == null || role.isEmpty()) {
	                role = oidcUser.getClaimAsString("usertype");
	            }
	            if (role == null || role.isEmpty()) {
	                role = oidcUser.getClaimAsString("roles");
	            }
	            if (role == null || role.isEmpty()) {
	                role = oidcUser.getClaimAsString("groups");
	            }
	            if (role == null || role.isEmpty()) {
	                role = "USER"; // Default role
	            }

	            // Normalize the role
	            role = role.equalsIgnoreCase("ADMIN") ? "ADMIN" : "USER";

                role = role.equalsIgnoreCase("admin") ? "admin" : "USER";

                // Debugging: Print user data before saving
                System.out.println("User extracted: " + username + " | Email: " + email + " | Role: " + role);

                // Find or create user
                Optional<UserDetails> userOpt = userRepository.findByEmail(email);
                UserDetails user = userOpt.orElseGet(() -> userRepository.findByUsername(username).orElse(new UserDetails()));

                // Update user details
                user.setEmail(email);
                user.setDisplayName(oidcUser.getClaimAsString("name"));
                user.setBirthdate(birthdate);
	            user.setUserStatus(userStatus);
	            user.setUpdatedAt(updatedAt);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setUsername(username);
                user.setNickname(nickname);
                user.setUserType(role);
                user.setSub(sub);

                // Save the user in the database
                userRepository.save(user);
                System.out.println("User saved: " + user.getUsername());

                // Assign role-based authorities
                Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());
                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

                return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }
        };
    }
}