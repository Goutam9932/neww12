package com.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
@EnableWebSecurity
public class OAuth2LoginConfig {

    @Value("${spring.security.oauth2.client.registration.wso2.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.wso2.client-secret}")
    private String clientSecret;

    @Bean
    public ClientRegistration clientRegistration() {

        return ClientRegistration.withRegistrationId("wso2")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/wso2")
                .scope("openid", "profile", "email")
                .authorizationUri("https://wso2.cnxy.in/oauth2/authorize")
                .tokenUri("https://wso2.cnxy.in/oauth2/token")
                .userInfoUri("https://wso2.cnxy.in/oauth2/userinfo")
                .jwkSetUri("https://wso2.cnxy.in/oauth2/jwks")
                .userNameAttributeName("given_name")
                .clientName("WSO2")
                .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(clientRegistration());
    }
}