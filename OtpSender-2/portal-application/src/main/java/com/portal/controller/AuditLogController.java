package com.portal.controller;

import com.portal.entity.AuditLog;
import com.portal.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // Log Request DTO
    public static class AuditRequest {
        private String applicationName;
        private String applicationUrl;

        public String getApplicationName() { return applicationName; }
        public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
        public String getApplicationUrl() { return applicationUrl; }
        public void setApplicationUrl(String applicationUrl) { this.applicationUrl = applicationUrl; }
    }

    // Response DTO
    public static class AuditLogResponse {
        private Long id;
        private String username;
        private String applicationName;
        private String applicationUrl;
        private String accessedAt;
        private String ipAddress;

        public AuditLogResponse(AuditLog log) {
            this.id = log.getId();
            this.username = log.getUsername();
            this.applicationName = log.getApplicationName();
            this.applicationUrl = log.getApplicationUrl();
            this.ipAddress = log.getIpAddress();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.accessedAt = log.getAccessedAt().format(formatter);
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getApplicationName() { return applicationName; }
        public String getApplicationUrl() { return applicationUrl; }
        public String getAccessedAt() { return accessedAt; }
        public String getIpAddress() { return ipAddress; }
    }

    @PostMapping("/log")
    public ResponseEntity<?> logAccess(@RequestBody AuditRequest auditRequest, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        AuditLog log = new AuditLog(
            username,
            auditRequest.getApplicationName(),
            auditRequest.getApplicationUrl(),
            ipAddress
        );

        auditLogRepository.save(log);

        return ResponseEntity.ok(Map.of("message", "Access logged successfully"));
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getAuditLogs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied"));
        }

        List<AuditLog> logs = auditLogRepository.findAllByOrderByAccessedAtDesc();
        List<AuditLogResponse> response = logs.stream()
            .map(AuditLogResponse::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
