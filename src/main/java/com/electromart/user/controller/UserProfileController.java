package com.electromart.user.controller;

import com.electromart.common.dto.ApiResponse;
import com.electromart.user.dto.*;
import com.electromart.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile() {
        UserProfileResponse response = userProfileService.getCurrentUserProfile();
        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("User profile retrieved successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
            @Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.updateUserProfile(request);
        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("Profile updated successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        userProfileService.changePassword(request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password changed successfully")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
    
    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<UserActivityResponse>> getRecentActivity() {
        UserActivityResponse response = userProfileService.getRecentActivity();
        return ResponseEntity.ok(
                ApiResponse.<UserActivityResponse>builder()
                        .success(true)
                        .message("Recent activity retrieved")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount() {
        userProfileService.deactivateAccount();
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Account deactivated successfully")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

  @PostMapping("/request-reactivation")
@PreAuthorize("permitAll()")
public ResponseEntity<ApiResponse<ReactivationResponse>> requestReactivation(
        @Valid @RequestBody RequestReactivationRequest request) {
    ReactivationResponse responseData = userProfileService.requestReactivation(request.getEmail());
    return ResponseEntity.ok(
            ApiResponse.<ReactivationResponse>builder()
                    .success(true)
                    .message("Reactivation details")
                    .timestamp(LocalDateTime.now())
                    .data(responseData)
                    .build());
}
    
    
    @PostMapping("/reactivate")
    @PreAuthorize("permitAll()") 
    public ResponseEntity<ApiResponse<Void>> reactivateAccount(
        @Valid @RequestBody ReactivateAccountRequest request) {
        userProfileService.reactivateAccount(request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Account reactivation initiated")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
    
    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> getUserPreferences() {
        UserPreferencesResponse response = userProfileService.getUserPreferences();
        return ResponseEntity.ok(
                ApiResponse.<UserPreferencesResponse>builder()
                        .success(true)
                        .message("User preferences retrieved")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> updateUserPreferences(
            @Valid @RequestBody UserPreferencesRequest request) {
        UserPreferencesResponse response = userProfileService.updateUserPreferences(request);
        return ResponseEntity.ok(
                ApiResponse.<UserPreferencesResponse>builder()
                        .success(true)
                        .message("Preferences updated successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
}