package com.electromart.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken; // Add this
    private String role;
    private String name;
    private String email;
    private Long userId;
    private Date tokenExpiry; // Change from LocalDateTime to Date
    private Date refreshTokenExpiry; // Add this
    
    // Constructor without refresh token for backward compatibility
    public AuthResponse(String token, String role, String name, String email, Long userId, Date tokenExpiry) {
        this.token = token;
        this.role = role;
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.tokenExpiry = tokenExpiry;
    }
}