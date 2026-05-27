package com.electromart.auth.controller;

import com.electromart.common.dto.ApiResponse;
import com.electromart.auth.dto.AuthResponse;
import com.electromart.auth.dto.ForgotPasswordRequest;
import com.electromart.auth.dto.LoginRequest;
import com.electromart.auth.dto.RefreshTokenRequest;
import com.electromart.auth.dto.ResetPasswordRequest;
import com.electromart.auth.dto.SignupRequest;
import com.electromart.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message("User registered successfully. Please verify your email.")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        return ResponseEntity.ok(authService.googleLogin(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String token) {
        // Add token blacklisting/invalidation logic if needed
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message("Logged out successfully")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message("If an account exists with this email, a password reset link has been sent")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message("Password reset successful. You can now login with your new password")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message("Email verified successfully")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Object>> resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message("Verification email sent")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
    
    @GetMapping("/validate-reset-token")
    public ResponseEntity<ApiResponse<Object>> validateResetToken(@RequestParam String token) {
        boolean isValid = authService.validatePasswordResetToken(token);
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message(isValid ? "Token is valid" : "Token is invalid or expired")
                .data(Map.of("valid", isValid))
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}