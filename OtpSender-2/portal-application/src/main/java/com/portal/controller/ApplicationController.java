package com.portal.controller;

import com.portal.entity.Application;
import com.portal.entity.Role;
import com.portal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    // Response DTO
    public static class ApplicationResponse {
        private Long id;
        private String name;
        private String description;
        private String url;
        private String iconName;
        private String category;
        private String themeColor;
        private List<String> allowedRoles;

        public ApplicationResponse(Application app) {
            this.id = app.getId();
            this.name = app.getName();
            this.description = app.getDescription();
            this.url = app.getUrl();
            this.iconName = app.getIconName();
            this.category = app.getCategory();
            this.themeColor = app.getThemeColor();
            this.allowedRoles = app.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getUrl() { return url; }
        public String getIconName() { return iconName; }
        public String getCategory() { return category; }
        public String getThemeColor() { return themeColor; }
        public List<String> getAllowedRoles() { return allowedRoles; }
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAuthorizedApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roleNames = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        List<Application> apps;
        // Admins can see all tiles, standard users see only their assigned tiles
        if (roleNames.contains("ROLE_ADMIN")) {
            apps = applicationRepository.findAll();
        } else {
            apps = applicationRepository.findByRoles(roleNames);
        }

        List<ApplicationResponse> response = apps.stream()
            .map(ApplicationResponse::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
