package com.electromart.admin.service.policy;

import org.springframework.stereotype.Component;

import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;
import com.electromart.common.exception.AccessDeniedException;
import com.electromart.common.exception.BusinessException;


@Component
public class UserRolePolicy {

    /* =========================================================
       COMMON ADMIN OPERATION VALIDATION
       ========================================================= */

    public void validateAdminOperation(User admin, User targetUser) {

        if (admin == null || targetUser == null) {
            throw new IllegalArgumentException("Admin or target user cannot be null");
        }

        if (admin.getId().equals(targetUser.getId())) {
            throw new AccessDeniedException(
                    "Admins cannot modify their own account");
        }

        Role adminRole = admin.getRole();
        Role targetRole = targetUser.getRole();

        if (adminRole != Role.ADMIN && adminRole != Role.SUPER_ADMIN) {
            throw new AccessDeniedException(
                    "You are not authorized to perform this operation");
        }

        if (adminRole == Role.ADMIN && targetRole == Role.SUPER_ADMIN) {
            throw new AccessDeniedException(
                    "Admin cannot modify Super Admin account");
        }
    }

    /* =========================================================
       ROLE CHANGE VALIDATION
       ========================================================= */

    public void validateRoleChange(User admin,
                                   User targetUser,
                                   Role newRole) {

        validateAdminOperation(admin, targetUser);

        if (newRole == null) {
            throw new BusinessException("New role must be provided");
        }

        Role adminRole = admin.getRole();
        Role currentRole = targetUser.getRole();

        if (currentRole == newRole) {
            throw new BusinessException("User already has the specified role");
        }

        // Only SUPER_ADMIN can assign SUPER_ADMIN role
        if (newRole == Role.SUPER_ADMIN && adminRole != Role.SUPER_ADMIN) {
            throw new AccessDeniedException(
                    "Only Super Admin can assign Super Admin role");
        }

        // ADMIN cannot promote another ADMIN to SUPER_ADMIN
        if (adminRole == Role.ADMIN && newRole == Role.SUPER_ADMIN) {
            throw new AccessDeniedException(
                    "Admin cannot promote user to Super Admin");
        }
    }

    /* =========================================================
       PERMANENT DELETION VALIDATION
       ========================================================= */

    public void validatePermanentDeletion(User admin, User targetUser) {

        validateAdminOperation(admin, targetUser);

        if (!Boolean.TRUE.equals(targetUser.isDeleted())) {
            throw new BusinessException(
                    "User must be soft-deleted before permanent deletion");
        }

        if (targetUser.getRole() == Role.SUPER_ADMIN) {
            throw new AccessDeniedException(
                    "Super Admin account cannot be permanently deleted");
        }
    }
}
