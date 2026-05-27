// package com.electromart.admin.controller;

// import com.electromart.admin.dto.*;
// import com.electromart.admin.service.UserManagementService;
// import com.electromart.auth.dto.AuthResponse;
// import com.electromart.common.dto.ApiResponse;
// import com.electromart.common.entity.Role;
// import com.electromart.user.dto.UserResponse;
// import com.electromart.user.dto.UserStatusUpdateRequest;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// import org.springframework.core.io.Resource;
// import org.springframework.data.domain.Page;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// //import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDateTime;
// import java.util.List;

// @RestController
// @RequestMapping("/api/admin/users")
// @RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')")
// public class UserManagementController {
    
//     private final UserManagementService userManagementService;
    
//     @GetMapping
//     public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(defaultValue = "createdAt") String sortBy,
//             @RequestParam(defaultValue = "desc") String direction) {
        
//         Page<UserResponse> users = userManagementService.getAllUsers(page, size, sortBy, direction);
//         return ResponseEntity.ok(
//                 ApiResponse.<Page<UserResponse>>builder()
//                         .success(true)
//                         .message("Users retrieved successfully")
//                         .timestamp(LocalDateTime.now())
//                         .data(users)
//                         .build());
//     }
    
//     @GetMapping("/search")
//     public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
//             @RequestParam String query,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
        
//         Page<UserResponse> users = userManagementService.searchUsers(query, page, size);
//         return ResponseEntity.ok(
//                 ApiResponse.<Page<UserResponse>>builder()
//                         .success(true)
//                         .message("Search results retrieved")
//                         .timestamp(LocalDateTime.now())
//                         .data(users)
//                         .build());
//     }
    
//     @GetMapping("/role/{role}")
//     public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByRole(
//             @PathVariable Role role,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
        
//         Page<UserResponse> users = userManagementService.getUsersByRole(role, page, size);
//         return ResponseEntity.ok(
//                 ApiResponse.<Page<UserResponse>>builder()
//                         .success(true)
//                         .message("Users by role retrieved")
//                         .timestamp(LocalDateTime.now())
//                         .data(users)
//                         .build());
//     }
    
//     @GetMapping("/status/{enabled}")
//     public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByStatus(
//             @PathVariable boolean enabled,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
        
//         Page<UserResponse> users = userManagementService.getUsersByStatus(enabled, page, size);
//         return ResponseEntity.ok(
//                 ApiResponse.<Page<UserResponse>>builder()
//                         .success(true)
//                         .message("Users by status retrieved")
//                         .timestamp(LocalDateTime.now())
//                         .data(users)
//                         .build());
//     }
    
//     @GetMapping("/{userId}")
//     public ResponseEntity<ApiResponse<UserResponse>> getUserById(
//             @PathVariable Long userId) {
//         UserResponse user = userManagementService.getUserById(userId);
//         return ResponseEntity.ok(
//                 ApiResponse.<UserResponse>builder()
//                         .success(true)
//                         .message("User retrieved successfully")
//                         .timestamp(LocalDateTime.now())
//                         .data(user)
//                         .build());
//     }
    
//     @GetMapping("/{userId}/detail")
//     public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
//             @PathVariable Long userId) {
//         UserDetailResponse userDetail = userManagementService.getUserDetail(userId);
//         return ResponseEntity.ok(
//                 ApiResponse.<UserDetailResponse>builder()
//                         .success(true)
//                         .message("User details retrieved successfully")
//                         .timestamp(LocalDateTime.now())
//                         .data(userDetail)
//                         .build());
//     }
    
//     @PutMapping("/{userId}/status")
//     public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
//             @PathVariable Long userId,
//             @Valid @RequestBody UserStatusUpdateRequest request) {
//         UserResponse response = userManagementService.updateUserStatus(userId, request);
//         return ResponseEntity.ok(
//                 ApiResponse.<UserResponse>builder()
//                         .success(true)
//                         .message("User status updated successfully")
//                         .timestamp(LocalDateTime.now())
//                         .data(response)
//                         .build());
//     }
    
//     @PutMapping("/{userId}/role")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
//             @PathVariable Long userId,
//             @Valid @RequestBody RoleUpdateRequest request) {
//         UserResponse response = userManagementService.updateUserRole(userId, request);
//         return ResponseEntity.ok(
//                 ApiResponse.<UserResponse>builder()
//                         .success(true)
//                         .message("User role updated successfully")
//                         .timestamp(LocalDateTime.now())
//                         .data(response)
//                         .build());
//     }
    
//     @DeleteMapping("/{userId}")
//     @PreAuthorize("hasRole('SUPER_ADMIN')")
//     public ResponseEntity<ApiResponse<Void>> deleteUser(
//             @PathVariable Long userId) {
//         userManagementService.deleteUser(userId);
//         return ResponseEntity.ok(
//                 ApiResponse.<Void>builder()
//                         .success(true)
//                         .message("User deleted successfully")
//                         .timestamp(LocalDateTime.now())
//                         .build());
//     }
    
  
// @
    
//     // AdminController.java

    
//     GetMapping("/stats")
//     public ResponseEntity<ApiResponse<AdminStatsResponse>> getAdminStats() {
//         AdminStatsResponse stats = userManagementService.getAdminStats();
//         return ResponseEntity.ok(
//                 ApiResponse.<AdminStatsResponse>builder()
//                         .success(true)
//                         .message("Admin statistics retrieved")
//                         .timestamp(LocalDateTime.now())
//                         .data(stats)
//                         .build());
//     }
    
//     @GetMapping("/{userId}/activity")
//     public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivity(
//             @PathVariable Long userId) {
//         List<UserActivityResponse> activities = userManagementService.getUserActivity(userId);
//         return ResponseEntity.ok(
//                 ApiResponse.<List<UserActivityResponse>>builder()
//                         .success(true)
//                         .message("User activity retrieved")
//                         .timestamp(LocalDateTime.now())
//                         .data(activities)
//                         .build());
//     }
    
//     @GetMapping("/recent")
//     public ResponseEntity<ApiResponse<List<UserResponse>>> getRecentRegistrations(
//             @RequestParam(defaultValue = "10") int limit) {
//         List<UserResponse> recentUsers = userManagementService.getRecentRegistrations(limit);
//         return ResponseEntity.ok(
//                 ApiResponse.<List<UserResponse>>builder()
//                         .success(true)
//                         .message("Recent registrations retrieved")
//                         .timestamp(LocalDateTime.now())
//                         .data(recentUsers)
//                         .build());
//     }
    
//     // TODO: Implement these methods
    
//     @GetMapping("/export")
//     public ResponseEntity<Resource> exportUsers(@RequestParam(defaultValue = "csv") String format) {
//         // Implementation for exporting users to CSV/Excel
//         // This would require additional service method
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users." + format)
//                 .contentType(MediaType.parseMediaType("application/" + format))
//                 .body(null); // Return the resource
//     }
    
//     @PostMapping("/{userId}/impersonate")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<ApiResponse<AuthResponse>> impersonateUser(
//             @PathVariable Long userId) {
//         // This would require AuthService to generate token for impersonated user
//         // Implementation depends on your JWT setup
//         return ResponseEntity.ok(
//                 ApiResponse.<AuthResponse>builder()
//                         .success(true)
//                         .message("Impersonation token generated")
//                         .timestamp(LocalDateTime.now())
//                         .data(null) // Would be the AuthResponse
//                         .build());
//     }
    
//     @PostMapping("/{userId}/reset-password")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<ApiResponse<Void>> adminResetPassword(
//             @PathVariable Long userId) {
//         // This would trigger password reset email for the user
//         // Similar to forgot-password but initiated by admin
//         return ResponseEntity.ok(
//                 ApiResponse.<Void>builder()
//                         .success(true)
//                         .message("Password reset initiated for user")
//                         .timestamp(LocalDateTime.now())
//                         .build());
//     }
    
//     @GetMapping("/count/active")
//     public ResponseEntity<ApiResponse<Long>> getActiveUserCount() {
//         long count = userManagementService.getAdminStats().getActiveUsers();
//         return ResponseEntity.ok(
//                 ApiResponse.<Long>builder()
//                         .success(true)
//                         .message("Active user count")
//                         .timestamp(LocalDateTime.now())
//                         .data(count)
//                         .build());
//     }
    
//     @GetMapping("/deleted")
//     @PreAuthorize("hasRole('SUPER_ADMIN')")
//     public ResponseEntity<ApiResponse<Page<UserResponse>>> getDeletedUsers(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
//         // This would require a new method in service to get deleted users
//         return ResponseEntity.ok(
//                 ApiResponse.<Page<UserResponse>>builder()
//                         .success(true)
//                         .message("Deleted users retrieved")
//                         .timestamp(LocalDateTime.now())
//                         .data(Page.empty()) // Implement this
//                         .build());
//     }
    
//     @PutMapping("/{userId}/restore")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<ApiResponse<UserResponse>> restoreUser(
//             @PathVariable Long userId) {
//         // This would restore a soft-deleted user
//         UserStatusUpdateRequest request = new UserStatusUpdateRequest();
//         request.setDeleted(false);
//         request.setEnabled(true);
        
//         UserResponse response = userManagementService.updateUserStatus(userId, request);
//         return ResponseEntity.ok(
//                 ApiResponse.<UserResponse>builder()
//                         .success(true)
//                         .message("User restored successfully")
//                         .timestamp(LocalDateTime.now())
//                         .data(response)
//                         .build());
//     }
// }


// package com.electromart.admin.controller;

package com.electromart.admin.controller;

import com.electromart.admin.dto.*;
import com.electromart.admin.service.facade.UserAdminFacade;
import com.electromart.common.dto.ApiResponse;
import com.electromart.common.entity.Role;
import com.electromart.user.dto.UserResponse;
import com.electromart.user.dto.UserStatusUpdateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserAdminFacade userAdminFacade;

    /* ===================== USER LISTING ===================== */

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return ok(userAdminFacade.getAllUsers(page, size, sortBy, direction),
                "Users retrieved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ok(userAdminFacade.searchUsers(query, page, size),
                "Search results retrieved");
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ok(userAdminFacade.getUsersByRole(role, page, size),
                "Users by role retrieved");
    }

    @GetMapping("/status/{enabled}")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByStatus(
            @PathVariable boolean enabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ok(userAdminFacade.getUsersByStatus(enabled, page, size),
                "Users by status retrieved");
    }

    /* ===================== USER DETAIL ===================== */

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        return ok(userAdminFacade.getUserById(userId),
                "User retrieved successfully");
    }

    @GetMapping("/{userId}/detail")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(@PathVariable Long userId) {
        return ok(userAdminFacade.getUserDetail(userId),
                "User details retrieved");
    }

    @GetMapping("/{userId}/activity")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivity(
            @PathVariable Long userId) {

        return ok(userAdminFacade.getUserActivity(userId),
                "User activity retrieved");
    }

    /* ===================== USER MUTATIONS ===================== */

    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {

        return ok(userAdminFacade.updateUserStatus(userId, request),
                "User status updated successfully");
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody RoleUpdateRequest request) {

        return ok(userAdminFacade.updateUserRole(userId, request),
                "User role updated successfully");
    }

    @PutMapping("/{userId}/restore")
    public ResponseEntity<ApiResponse<UserResponse>> restoreUser(@PathVariable Long userId) {
        return ok(userAdminFacade.restoreUser(userId),
                "User restored successfully");
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userAdminFacade.deleteUser(userId);
        return ok(null, "User deleted successfully");
    }

    /* ===================== ADMIN DASHBOARD ===================== */

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getAdminStats() {
        return ok(userAdminFacade.getAdminStats(),
                "Admin statistics retrieved");
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getRecentRegistrations(
            @RequestParam(defaultValue = "10") int limit) {

        return ok(userAdminFacade.getRecentRegistrations(limit),
                "Recent registrations retrieved");
    }

    @GetMapping("/count/active")
    public ResponseEntity<ApiResponse<Long>> getActiveUserCount() {
        return ok(userAdminFacade.getActiveUserCount(),
                "Active user count");
    }

    /* ===================== STUB / FUTURE ===================== */

    @GetMapping("/export")
    public ResponseEntity<Resource> exportUsers(
            @RequestParam(defaultValue = "csv") String format) {

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=users." + format)
                .contentType(MediaType.parseMediaType("application/" + format))
                .body(null);
    }

    /* ===================== COMMON RESPONSE ===================== */

    private <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .data(data)
                        .build());
    }
}

