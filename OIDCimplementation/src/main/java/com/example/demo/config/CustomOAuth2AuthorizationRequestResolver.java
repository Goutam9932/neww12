package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import jakarta.servlet.http.HttpServletRequest;

public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

	private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;
	private final String acrValue;

	public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
			String acrValue) {
		this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
				"/oauth2/authorization");
		this.acrValue = acrValue;
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		OAuth2AuthorizationRequest authRequest = defaultResolver.resolve(request);
		return modifyAuthorizationRequest(authRequest);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		OAuth2AuthorizationRequest authRequest = defaultResolver.resolve(request, clientRegistrationId);
		return modifyAuthorizationRequest(authRequest);
	}
   
	
	
	private OAuth2AuthorizationRequest modifyAuthorizationRequest(OAuth2AuthorizationRequest authRequest) {
	    if (authRequest == null) {
	        return null;
	    }

	    // Identify the provider (Gluu or WSO2)
	    String authorizationUri = authRequest.getAuthorizationUri();
	    boolean isGluu = authorizationUri.contains("oxauth");

	    // Create a new map for additional parameters
	    Map<String, Object> additionalParameters = new HashMap<>(authRequest.getAdditionalParameters());

	    // Add acr_values only for Gluu
	    if (isGluu) {
	        additionalParameters.put("acr_values", acrValue);
	    }

	    return OAuth2AuthorizationRequest.from(authRequest)
	            .additionalParameters(additionalParameters)
	            .build();
	}

}
