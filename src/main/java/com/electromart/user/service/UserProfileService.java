package com.electromart.user.service;

import com.electromart.address.repository.AddressRepository;
import com.electromart.common.entity.User;
import com.electromart.user.dto.*;
import com.electromart.user.exception.PasswordMismatchException;
import com.electromart.user.exception.UserNotFoundException;
import com.electromart.user.exception.UserOperationException;
import com.electromart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    
    // ===== HELPER METHODS =====
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    private User getUserForReactivation(String email) {
        return userRepository.findByEmailIncludingDisabled(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
    
    // ===== PROTECTED METHODS (require authentication) =====
    
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return convertToProfileResponse(user);
    }
    
    @Transactional
    public UserProfileResponse updateUserProfile(UserProfileRequest request) {
        User user = getCurrentUser();
        
        // Validate and update name
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (newName.length() < 2 || newName.length() > 100) {
                throw new UserOperationException("Name must be between 2 and 100 characters");
            }
            user.setName(newName);
        }
        
        // Validate and update phone
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String newPhone = request.getPhone().trim();
            if (!newPhone.matches("^[0-9]{10}$")) {
                throw new UserOperationException("Invalid phone number format. Must be 10 digits.");
            }
            user.setPhone(newPhone);
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User profile updated for: {}", user.getEmail());
        
        return convertToProfileResponse(updatedUser);
    }
    
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Current password is incorrect");
        }
        
        // Check if new password is same as current
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new UserOperationException("New password cannot be same as current password");
        }
        
        // Validate new password strength
        if (!isValidPassword(request.getNewPassword())) {
            throw new UserOperationException(
                "Password must contain at least 8 characters, one uppercase, one lowercase, and one number"
            );
        }
        
        // Verify new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("New password and confirmation do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password changed for user: {}", user.getEmail());
        
        // TODO: Send email notification about password change
    }
    
    @Transactional
    public void deactivateAccount() {
        User user = getCurrentUser();
        
        // Check if user has any pending orders
        // TODO: Check if user has pending orders before deactivation
        
        // Generate reactivation token BEFORE deactivating
        String reactivationToken = UUID.randomUUID().toString();
        user.setReactivationToken(reactivationToken);
        user.setReactivationTokenExpiry(LocalDateTime.now().plusDays(30));
        
        // Soft deactivate (disable)
        user.setEnabled(false);
        userRepository.save(user);
        
        log.info("Account deactivated for user: {}", user.getEmail());
        
        // TODO: Send deactivation confirmation email with reactivation link
        // emailService.sendDeactivationEmail(user.getEmail(), reactivationToken);
    }
    
    @Transactional(readOnly = true)
    public UserActivityResponse getRecentActivity() {
        User user = getCurrentUser();
        
        // TODO: Implement actual activity tracking from different services
        // This would aggregate data from OrderService, ReviewService, etc.
        
        return UserActivityResponse.builder()
                .userId(user.getId())
                .lastLogin(LocalDateTime.now().minusHours(2))
                .totalOrders(5) // TODO: Get from OrderService
                .totalReviews(3) // TODO: Get from ReviewService
                .totalAddresses(addressRepository.findByUserAndDeletedFalse(user).size())
                .profileUpdatedAt(user.getUpdatedAt())
                .build();
    }
    
    @Transactional(readOnly = true)
    public UserPreferencesResponse getUserPreferences() {
        User user = getCurrentUser();
        
        // TODO: Store preferences in a separate entity or JSON field
        return UserPreferencesResponse.builder()
                .userId(user.getId())
                .emailNotifications(true)
                .smsNotifications(false)
                .newsletterSubscription(true)
                .preferredLanguage("en")
                .currency("USD")
                .theme("light")
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Transactional
    public UserPreferencesResponse updateUserPreferences(UserPreferencesRequest request) {
        User user = getCurrentUser();
        
        // TODO: Update user preferences in database
        // This could be stored in a separate UserPreferences entity or JSON field in User
        
        log.info("User preferences updated for: {}", user.getEmail());
        
        return UserPreferencesResponse.builder()
                .userId(user.getId())
                .emailNotifications(request.isEmailNotifications())
                .smsNotifications(request.isSmsNotifications())
                .newsletterSubscription(request.isNewsletterSubscription())
                .preferredLanguage(request.getPreferredLanguage())
                .currency(request.getCurrency())
                .theme(request.getTheme())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    // ===== PUBLIC METHODS (no authentication required) =====
    
    @Transactional
    public ReactivationResponse requestReactivation(String email) {
    log.info("Requesting reactivation for email: {}", email);
    
    // Find user (including disabled users)
    User user = getUserForReactivation(email);
    
    // Check if user is actually deleted (soft delete)
    if (user.isDeleted()) {
        throw new UserOperationException("This account has been permanently deleted and cannot be reactivated");
    }
    
    // If user is already active, throw exception
    if (user.isEnabled()) {
        throw new UserOperationException("Account is already active");
    }
    
    // Check if user has a valid reactivation token already
    if (user.getReactivationToken() != null && 
        user.getReactivationTokenExpiry() != null &&
        user.getReactivationTokenExpiry().isAfter(LocalDateTime.now())) {
        
        log.info("User {} already has a valid reactivation token, resending email", email);
        
        // Create reactivation link with existing token
        String reactivationLink = createReactivationLink(email, user.getReactivationToken());
        
        // TODO: Resend email with existing token
        // emailService.sendReactivationEmail(email, user.getReactivationToken());
        
        return ReactivationResponse.builder()
                .email(email)
                .reactivationToken(user.getReactivationToken())
                .reactivationLink(reactivationLink)
                .expiresAt(user.getReactivationTokenExpiry())
                .message("Using existing valid token. Token expires at: " + user.getReactivationTokenExpiry())
                .build();
    }
    
    // Generate new reactivation token
    String reactivationToken = UUID.randomUUID().toString();
    user.setReactivationToken(reactivationToken);
    user.setReactivationTokenExpiry(LocalDateTime.now().plusHours(24));
    userRepository.save(user);
    
    log.info("New reactivation token generated for user: {}", email);
    
    // Create reactivation link
    String reactivationLink = createReactivationLink(email, reactivationToken);
    
    // TODO: Send reactivation email with token
    // emailService.sendReactivationEmail(email, reactivationToken);
    
    return ReactivationResponse.builder()
            .email(email)
            .reactivationToken(reactivationToken)
            .reactivationLink(reactivationLink)
            .expiresAt(user.getReactivationTokenExpiry())
            .message("Use this token to reactivate your account via /api/users/  profile/reactivate endpoint")
            .build();
}
    
    @Transactional
    public void reactivateAccount(ReactivateAccountRequest request) {
        log.info("Attempting to reactivate account for email: {}", request.getEmail());
        
        // Find user (including disabled users)
        User user = getUserForReactivation(request.getEmail());
        
        // Check if user is deleted
        if (user.isDeleted()) {
            throw new UserOperationException("This account has been permanently deleted and cannot be reactivated");
        }
        
        // If user is already active, throw exception
        if (user.isEnabled()) {
            throw new UserOperationException("Account is already active");
        }
        
        // Validate reactivation token
        if (user.getReactivationToken() == null) {
            throw new UserOperationException("No reactivation token found for this account. Please request a new reactivation link.");
        }
        
        if (!user.getReactivationToken().equals(request.getToken())) {
            log.warn("Invalid reactivation token for user: {}", request.getEmail());
            throw new UserOperationException("Invalid reactivation token");
        }
        
        if (user.getReactivationTokenExpiry() == null) {
            throw new UserOperationException("Reactivation token has expired. Please request a new reactivation link.");
        }
        
        if (user.getReactivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new UserOperationException("Reactivation token has expired. Please request a new reactivation link.");
        }
        
        // Reactivate the account
        user.setEnabled(true);
        user.setReactivationToken(null);
        user.setReactivationTokenExpiry(null);
        userRepository.save(user);
        
        log.info("Account successfully reactivated for user: {}", user.getEmail());
        
        // TODO: Send reactivation confirmation email
        // emailService.sendAccountReactivatedEmail(user.getEmail());
    }

    private String createReactivationLink(String email, String token) {
    return String.format(
        "http://localhost:8080/api/users/profile/reactivate-test?email=%s&token=%s",
        email, token
    );
}
    
    // ===== PRIVATE HELPER METHODS =====
    
    private UserProfileResponse convertToProfileResponse(User user) {
        boolean hasDefaultAddress = addressRepository.findByUserAndIsDefaultTrueAndDeletedFalse(user).isPresent();
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .emailVerified(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .hasDefaultAddress(hasDefaultAddress)
                .build();
    }
    
    private boolean isValidPassword(String password) {
        // At least 8 chars, one uppercase, one lowercase, one number
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(pattern);
    }
}