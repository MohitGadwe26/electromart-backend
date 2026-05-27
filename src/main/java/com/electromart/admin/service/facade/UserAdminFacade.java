package com.electromart.admin.service.facade;

import com.electromart.admin.dto.*;
import com.electromart.admin.service.audit.AuditService;
import com.electromart.admin.service.domain.UserDomainService;
import com.electromart.admin.service.notification.NotificationService;
import com.electromart.admin.service.policy.UserRolePolicy;
import com.electromart.admin.service.policy.UserStatusPolicy;
import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;
import com.electromart.user.dto.UserResponse;
import com.electromart.user.dto.UserStatusUpdateRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminFacade {

    private final UserDomainService domainService;
    private final UserRolePolicy rolePolicy;
    private final UserStatusPolicy statusPolicy;
    private final AuditService auditService;
    private final NotificationService notificationService;

    /* ===================== READ OPERATIONS ===================== */

    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String direction) {
        return domainService.getAllUsers(page, size, sortBy, direction);
    }

    public Page<UserResponse> searchUsers(String query, int page, int size) {
        return domainService.searchUsers(query, page, size);
    }

    public Page<UserResponse> getUsersByRole(Role role, int page, int size) {
        return domainService.getUsersByRole(role, page, size);
    }

    public Page<UserResponse> getUsersByStatus(boolean enabled, int page, int size) {
        return domainService.getUsersByStatus(enabled, page, size);
    }

    public UserResponse getUserById(Long userId) {
        return domainService.getUserResponse(userId);
    }

    public UserDetailResponse getUserDetail(Long userId) {
        return domainService.getUserDetail(userId);
    }

    public List<UserActivityResponse> getUserActivity(Long userId) {
        return domainService.getUserActivity(userId);
    }

    public List<UserResponse> getRecentRegistrations(int limit) {
        return domainService.getRecentRegistrations(limit);
    }

    public AdminStatsResponse getAdminStats() {
        return domainService.getAdminStats();
    }

    public long getActiveUserCount() {
        return domainService.getAdminStats().getActiveUsers();
    }

    /* ===================== WRITE OPERATIONS ===================== */

    public UserResponse updateUserStatus(Long userId,
                                         UserStatusUpdateRequest request) {

        User admin = domainService.getCurrentAdmin();
        User user = domainService.getUser(userId);

        rolePolicy.validateAdminOperation(admin, user);
        statusPolicy.validateStatusChange(
                user,
                request.getEnabled(),
                request.getDeleted()
        );

        user.updateStatusChange(
                admin.getId(),
                request.getReason(),
                request.getEnabled(),
                request.getDeleted()
        );

        domainService.save(user);

        auditService.logUserStatusChange(admin, user, request.getReason());

        if (Boolean.TRUE.equals(request.getNotifyUsers())) {
            notificationService.notifyStatusChange(user);
        }

        return domainService.toUserResponse(user);
    }

    public UserResponse updateUserRole(Long userId, RoleUpdateRequest request) {

        User admin = domainService.getCurrentAdmin();
        User user = domainService.getUser(userId);

        rolePolicy.validateRoleChange(admin, user, request.getRole());

        Role oldRole = user.getRole();
        user.setRole(request.getRole());

        domainService.save(user);

        auditService.logUserRoleChange(admin, user, oldRole, request.getRole());
        notificationService.notifyRoleChange(user, oldRole);

        return domainService.toUserResponse(user);
    }

    public UserResponse restoreUser(Long userId) {

        User admin = domainService.getCurrentAdmin();
        User user = domainService.getUser(userId);

        rolePolicy.validateAdminOperation(admin, user);

        user.restore(admin.getId());

        domainService.save(user);

        auditService.logUserRestore(admin, user);
        notificationService.notifyRestore(user);

        return domainService.toUserResponse(user);
    }

    public void deleteUser(Long userId) {

        User admin = domainService.getCurrentAdmin();
        User user = domainService.getUser(userId);

        rolePolicy.validatePermanentDeletion(admin, user);

        domainService.softDelete(user, admin.getId());

        auditService.logUserDeletion(admin, user);
        notificationService.notifyDeletion(user);
    }
}
