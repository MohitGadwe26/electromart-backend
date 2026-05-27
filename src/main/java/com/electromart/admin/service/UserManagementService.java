// package com.electromart.admin.service;



// import com.electromart.admin.dto.*;
// import com.electromart.admin.exception.AdminOperationException;
// import com.electromart.admin.exception.InsufficientPrivilegeException;
// import com.electromart.address.repository.AddressRepository;
// import com.electromart.common.entity.Role;
// import com.electromart.common.entity.User;
// import com.electromart.user.dto.UserResponse;
// import com.electromart.user.dto.UserStatusUpdateRequest;
// import com.electromart.user.exception.UserNotFoundException;
// import com.electromart.user.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class UserManagementService {
    
//     private final UserRepository userRepository;
//     private final AddressRepository addressRepository;
    
//     private User getCurrentAdminUser() {
//         String email = SecurityContextHolder.getContext().getAuthentication().getName();
//         return userRepository.findByEmail(email)
//                 .orElseThrow(() -> new UserNotFoundException("Admin user not found"));
//     }
    
//     @Transactional(readOnly = true)
//     public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String direction) {
//         Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") 
//                 ? Sort.Direction.ASC : Sort.Direction.DESC;
        
//         Sort sort = Sort.by(sortDirection, sortBy);
//         if ("name".equals(sortBy)) {
//             sort = Sort.by(sortDirection, "name");
//         }
        
//         Pageable pageable = PageRequest.of(page, size, sort);
//         Page<User> users = userRepository.findByDeletedFalse(pageable);
        
//         return users.map(this::convertToUserResponse);
//     }
    
//     @Transactional(readOnly = true)
//     public Page<UserResponse> searchUsers(String query, int page, int size) {
//         Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//         Page<User> users = userRepository.searchUsers(query, pageable);
        
//         return users.map(this::convertToUserResponse);
//     }
    
//     @Transactional(readOnly = true)
//     public Page<UserResponse> getUsersByRole(Role role, int page, int size) {
//         Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//         Page<User> users = userRepository.findByRoleAndDeletedFalse(role, pageable);
        
//         return users.map(this::convertToUserResponse);
//     }
    
//     @Transactional(readOnly = true)
//     public Page<UserResponse> getUsersByStatus(boolean enabled, int page, int size) {
//         Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//         Page<User> users = userRepository.findByEnabledAndDeletedFalse(enabled, pageable);
        
//         return users.map(this::convertToUserResponse);
//     }
    
//     @Transactional(readOnly = true)
//     public UserResponse getUserById(Long userId) {
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
//         // Admins can see deleted users, but mark them as deleted
//         if (user.isDeleted()) {
//             log.warn("Admin {} accessed deleted user {}", 
//                     getCurrentAdminUser().getEmail(), user.getEmail());
//         }
        
//         return convertToUserResponse(user);
//     }
    
//     @Transactional(readOnly = true)
//     public UserDetailResponse getUserDetail(Long userId) {
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new UserNotFoundException("User not found"));
        
//         int addressCount = addressRepository.findByUserAndDeletedFalse(user).size();
//         List<AddressSummaryResponse> addresses = addressRepository.findByUserAndDeletedFalse(user)
//                 .stream()
//                 .map(address -> AddressSummaryResponse.builder()
//                         .id(address.getId())
//                         .street(address.getStreet())
//                         .city(address.getCity())
//                         .state(address.getState())
//                         .country(address.getCountry())
//                         .isDefault(address.isDefault())
//                         .createdAt(address.getCreatedAt())
//                         .build())
//                 .collect(Collectors.toList());
        
//         // Get user statistics (you can add more based on your needs)
//         int orderCount = 0; 
//         int reviewCount = 0; 
        
//         return UserDetailResponse.builder()
//                 .id(user.getId())
//                 .name(user.getName())
//                 .email(user.getEmail())
//                 .phone(user.getPhone())
//                 .role(user.getRole())
//                 .enabled(user.isEnabled())
//                 .deleted(user.isDeleted())
//                 .emailVerified(user.isEnabled())
//                 .createdAt(user.getCreatedAt())
//                 .updatedAt(user.getUpdatedAt())
//                 .lastLoginAt(null) 
//                 .addressCount(addressCount)
//                 .addresses(addresses)
//                 .orderCount(orderCount)
//                 .reviewCount(reviewCount)
//                 .totalSpent(0.0) 
//                 .build();
//     }
    
//      @Transactional
//     public UserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request) {
//         User adminUser = getCurrentAdminUser();
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new UserNotFoundException("User not found"));
        
//         // Prevent self-modification
//         if (adminUser.getId().equals(userId)) {
//             throw new AdminOperationException("You cannot modify your own status");
//         }
        
//         // Role-based restrictions
//         if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN) {
//             // Only SUPER_ADMIN can modify other admins
//             if (adminUser.getRole() != Role.SUPER_ADMIN) {
//                 throw new InsufficientPrivilegeException(
//                         "Only super admin can modify admin users");
//             }
//         }
        
//         // Update enabled status
//         if (request.getEnabled() != null) {
//             if (request.getEnabled() && user.isDeleted()) {
//                 throw new AdminOperationException("Cannot enable a deleted user");
//             }
//             user.setEnabled(request.getEnabled());
            
//             // Log the change
//             if (request.getEnabled()) {
//                 user.setReactivatedAt(LocalDateTime.now());
//                 user.setReactivatedBy(adminUser.getId());
//             }
            
//             log.info("User {} status changed to {} by admin {}", 
//                     user.getEmail(), request.getEnabled() ? "ENABLED" : "DISABLED", 
//                     adminUser.getEmail());
//         }
        
//         // Update deleted status
//         if (request.getDeleted() != null) {
//             if (request.getDeleted()) {
//                 // Soft delete - disable first
//                 user.setEnabled(false);
//                 user.setDeleted(true);
//                 user.setDeletedAt(LocalDateTime.now());
//                 user.setDeletedBy(adminUser.getId());
//                 log.info("User {} soft deleted by admin {}", 
//                         user.getEmail(), adminUser.getEmail());
//             } else {
//                 // Restore user
//                 user.setDeleted(false);
//                 user.setDeletedAt(null);
//                 user.setDeletedBy(null);
//                 log.info("User {} restored by admin {}", 
//                         user.getEmail(), adminUser.getEmail());
//             }
//         }
        
//         // Update reason if provided
//         if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
//             log.info("Status change reason for user {}: {}", 
//                     user.getEmail(), request.getReason());
//         }
        
//         User updatedUser = userRepository.save(user);
        
//         // Send notification if requested
//         if (Boolean.TRUE.equals(request.getNotifyUsers())) {
//             sendStatusUpdateNotification(user, request, adminUser);
//         }
        
//         return convertToUserResponse(updatedUser);
//     }
    
//     @Transactional
//     public UserResponse updateUserRole(Long userId, RoleUpdateRequest request) {
//         User adminUser = getCurrentAdminUser();
        
//         // Only SUPER_ADMIN can change roles
//         if (adminUser.getRole() != Role.ADMIN) {
//             throw new InsufficientPrivilegeException("Only super admin can change user roles");
//         }
        
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new UserNotFoundException("User not found"));
        
//         // Prevent self-modification
//         if (adminUser.getId().equals(userId)) {
//             throw new AdminOperationException("You cannot change your own role");
//         }
        
//         // Cannot demote last super admin
//         if (user.getRole() == Role.SUPER_ADMIN && request.getRole() != Role.SUPER_ADMIN) {
//             long superAdminCount = userRepository.findByRoleAndDeletedFalse(Role.SUPER_ADMIN, 
//                     PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
//             if (superAdminCount <= 1) {
//                 throw new AdminOperationException(
//                         "Cannot demote the last super admin. Promote another user first.");
//             }
//         }
        
//         user.setRole(request.getRole());
//         User updatedUser = userRepository.save(user);
        
//         log.info("User {} role changed from {} to {} by super admin {}", 
//                 user.getEmail(), user.getRole(), request.getRole(), adminUser.getEmail());
        
//         return convertToUserResponse(updatedUser);
//     }
    
//     @Transactional
//     public void deleteUser(Long userId) {
//         User adminUser = getCurrentAdminUser();
        
//         // Only SUPER_ADMIN can delete users permanently
//         if (adminUser.getRole() != Role.SUPER_ADMIN) {
//             throw new InsufficientPrivilegeException("Only super admin can delete users");
//         }
        
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new UserNotFoundException("User not found"));
        
//         // Prevent self-deletion
//         if (adminUser.getId().equals(userId)) {
//             throw new AdminOperationException("You cannot delete your own account");
//         }
        
//         // Cannot delete super admin unless there's another one
//         if (user.getRole() == Role.SUPER_ADMIN) {
//             long superAdminCount = userRepository.findByRoleAndDeletedFalse(Role.SUPER_ADMIN, 
//                     PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
//             if (superAdminCount <= 1) {
//                 throw new AdminOperationException(
//                         "Cannot delete the last super admin. Promote another user first.");
//             }
//         }
        
//         // Perform soft delete (or hard delete based on requirements)
//         user.setDeleted(true);
//         user.setEnabled(false);
//         userRepository.save(user);
        
//         log.warn("User {} permanently deleted by super admin {}", 
//                 user.getEmail(), adminUser.getEmail());
//     }

//      @Transactional
//     public BulkOperationResponse bulkUpdateUserStatus(BulkStatusUpdateRequest request) {
//         User adminUser = getCurrentAdminUser();
        
//         if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
//             throw new AdminOperationException("User IDs cannot be empty");
//         }
        
//         // Filter out admin user from the list
//         List<Long> filteredUserIds = request.getUserIds().stream()
//                 .filter(id -> !id.equals(adminUser.getId()))
//                 .collect(Collectors.toList());
        
//         if (filteredUserIds.isEmpty()) {
//             throw new AdminOperationException("No valid users to update");
//         }
        
//         int successCount = 0;
//         int failureCount = 0;
//         List<String> errors = new ArrayList<>();
//         List<User> updatedUsers = new ArrayList<>();
        
//         for (Long userId : filteredUserIds) {
//             try {
//                 // Create individual request for each user
//                 UserStatusUpdateRequest individualRequest = UserStatusUpdateRequest.builder()
//                         .enabled(request.getEnabled())
//                         .deleted(request.getDeleted())
//                         .reason(request.getReason())
//                         .notifyUsers(request.getNotifyUsers())
//                         .build();
                
//                 User user = userRepository.findById(userId)
//                         .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
                
//                 // Apply updates
//                 updateUserWithRequest(user, individualRequest, adminUser);
                
//                 userRepository.save(user);
//                 updatedUsers.add(user);
//                 successCount++;
                
//             } catch (Exception e) {
//                 failureCount++;
//                 errors.add("User ID " + userId + ": " + e.getMessage());
//                 log.error("Failed to update user {}: {}", userId, e.getMessage());
//             }
//         }
        
//         // Send bulk notification if requested
//         if (Boolean.TRUE.equals(request.getNotifyUsers()) && !updatedUsers.isEmpty()) {
//             sendBulkNotification(updatedUsers, request, adminUser);
//         }
        
//         // Create audit log for bulk operation
//         createBulkAuditLog(adminUser, filteredUserIds, request, successCount);
        
//         return BulkOperationResponse.builder()
//                 .total(request.getUserIds().size())
//                 .successful(successCount)
//                 .failed(failureCount)
//                 .errors(errors)
//                 .timestamp(LocalDateTime.now())
//                 .build();
//     }
    
//     // Helper method to update user with request
//     // private void updateUserWithRequest(User user, UserStatusUpdateRequest request, User adminUser) {
//     //     // Role-based restrictions
//     //     if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN) {
//     //         if (adminUser.getRole() != Role.SUPER_ADMIN) {
//     //             throw new InsufficientPrivilegeException(
//     //                     "Only super admin can modify admin users");
//     //         }
//     //     }
        
//     //     // Update enabled status
//     //     if (request.getEnabled() != null) {
//     //         if (request.getEnabled() && user.isDeleted()) {
//     //             throw new AdminOperationException("Cannot enable a deleted user");
//     //         }
//     //         user.setEnabled(request.getEnabled());
            
//     //         if (request.getEnabled()) {
//     //             user.setReactivatedAt(LocalDateTime.now());
//     //             user.setReactivatedBy(adminUser.getId());
//     //         }
//     //     }
        
//     //     // Update deleted status
//     //     if (request.getDeleted() != null) {
//     //         if (request.getDeleted()) {
//     //             user.setEnabled(false);
//     //             user.setDeleted(true);
//     //             user.setDeleted(LocalDateTime.now());
//     //             user.setDeletedBy(adminUser.getId());
//     //         } else {
//     //             user.setDeleted(false);
//     //             user.setDeleted(null);
//     //             user.setDeletedBy(null);
//     //         }
//     //     }
        
//     //     user.setUpdatedAt(LocalDateTime.now());
//     // }

//     // In UserManagementService.java
// private void updateUserWithRequest(User user, UserStatusUpdateRequest request, User adminUser) {
//     // Role-based restrictions
//     if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN) {
//         if (adminUser.getRole() != Role.SUPER_ADMIN) {
//             throw new InsufficientPrivilegeException(
//                     "Only super admin can modify admin users");
//         }
//     }
    
//     // Use entity helper methods
//     user.updateStatusChange(
//         adminUser.getId(),
//         request.getEnabled(),
//         request.getDeleted()
//     );
// }

//       // Send notification to user
//     private void sendStatusUpdateNotification(User user, UserStatusUpdateRequest request, User adminUser) {
//         // Implement your notification logic here
//         log.info("Sending notification to user {} about status change", user.getEmail());
//         // Example: emailService.sendStatusUpdateEmail(user, request, adminUser);
//     }
    
//     // Send bulk notification
//     private void sendBulkNotification(List<User> users, BulkStatusUpdateRequest request, User adminUser) {
//         log.info("Sending bulk notification to {} users", users.size());
//         // Example: emailService.sendBulkStatusUpdateEmail(users, request, adminUser);
//     }
    
//     // Create audit log
//     private void createBulkAuditLog(User adminUser, List<Long> userIds, BulkStatusUpdateRequest request, int successCount) {
//         log.info("Admin {} performed bulk update on {} users ({} successful)", 
//                 adminUser.getEmail(), userIds.size(), successCount);
//         // Example: auditLogService.logBulkAction(adminUser, userIds, request);
//     }
    
//     // @Transactional
//     // public BulkOperationResponse bulkUpdateUserStatus(List<Long> userIds, UserStatusUpdateRequest request) {
//     //     User adminUser = getCurrentAdminUser();
        
//     //     // Filter out admin user from the list
//     //     List<Long> filteredUserIds = userIds.stream()
//     //             .filter(id -> !id.equals(adminUser.getId()))
//     //             .collect(Collectors.toList());
        
//     //     if (filteredUserIds.isEmpty()) {
//     //         throw new AdminOperationException("No valid users to update");
//     //     }
        
//     //     int successCount = 0;
//     //     int failureCount = 0;
//     //     List<String> errors = new java.util.ArrayList<>();
        
//     //     for (Long userId : filteredUserIds) {
//     //         try {
//     //             updateUserStatus(userId, request);
//     //             successCount++;
//     //         } catch (Exception e) {
//     //             failureCount++;
//     //             errors.add("User ID " + userId + ": " + e.getMessage());
//     //             log.error("Failed to update user {}: {}", userId, e.getMessage());
//     //         }
//     //     }
        
//     //     return BulkOperationResponse.builder()
//     //             .total(userIds.size())
//     //             .successful(successCount)
//     //             .failed(failureCount)
//     //             .errors(errors)
//     //             .timestamp(LocalDateTime.now())
//     //             .build();
//     // }
    
//     @Transactional(readOnly = true)
//     public AdminStatsResponse getAdminStats() {
//         long totalUsers = userRepository.count();
//         long activeUsers = userRepository.countActiveUsers();
//         long verifiedUsers = userRepository.findByEnabledAndDeletedFalse(true, 
//                 PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
        
//         // Get recent registrations (last 7 days)
//         LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
//         long recentRegistrations = userRepository.findAll().stream()
//                 .filter(user -> user.getCreatedAt() != null && 
//                                user.getCreatedAt().isAfter(weekAgo))
//                 .count();
        
//         // Count by role
//         long userCount = userRepository.findByRoleAndDeletedFalse(Role.USER, 
//                 PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
//         long adminCount = userRepository.findByRoleAndDeletedFalse(Role.ADMIN, 
//                 PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
        
//         return AdminStatsResponse.builder()
//                 .totalUsers(totalUsers)
//                 .activeUsers(activeUsers)
//                 .verifiedUsers(verifiedUsers)
//                 .recentRegistrations(recentRegistrations)
//                 .userCount(userCount)
//                 .adminCount(adminCount)
//                 .generatedAt(LocalDateTime.now())
//                 .build();
//     }
    
//     @Transactional(readOnly = true)
//     public List<UserActivityResponse> getUserActivity(Long userId) {
//        // User user = userRepository.findById(userId)
//              //   .orElseThrow(() -> new UserNotFoundException("User not found"));
        
//         // Implement user activity tracking
//         // This could include: login history, order history, review history, etc.
        
//         return List.of(
//                 UserActivityResponse.builder()
//                         .activityType("PROFILE_UPDATE")
//                         .description("Updated profile information")
//                         .timestamp(LocalDateTime.now().minusDays(1))
//                         .build(),
//                 UserActivityResponse.builder()
//                         .activityType("LOGIN")
//                         .description("User logged in")
//                         .timestamp(LocalDateTime.now().minusHours(3))
//                         .build()
//         );
//     }
    
//     @Transactional(readOnly = true)
//     public List<UserResponse> getRecentRegistrations(int limit) {
//         List<User> recentUsers = userRepository.findAll().stream()
//                 .filter(user -> !user.isDeleted())
//                 .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
//                 .limit(limit)
//                 .collect(Collectors.toList());
        
//         return recentUsers.stream()
//                 .map(this::convertToUserResponse)
//                 .collect(Collectors.toList());
//     }
    
//     private UserResponse convertToUserResponse(User user) {
//         int addressCount = addressRepository.findByUserAndDeletedFalse(user).size();
        
//         return UserResponse.builder()
//                 .id(user.getId())
//                 .name(user.getName())
//                 .email(user.getEmail())
//                 .phone(user.getPhone())
//                 .role(user.getRole())
//                 .enabled(user.isEnabled())
//                 .deleted(user.isDeleted())
//                 .createdAt(user.getCreatedAt())
//                 .updatedAt(user.getUpdatedAt())
//                 .addressCount(addressCount)
//                 .build();
//     }
// }


