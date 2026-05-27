package com.electromart.admin.service.policy;

import org.springframework.stereotype.Component;

import com.electromart.common.entity.User;
import com.electromart.common.exception.BusinessException;

@Component
public class UserStatusPolicy {

    /**
     * Validate whether a status change is allowed
     */
    public void validateStatusChange(User user,
                                     Boolean newEnabled,
                                     Boolean newDeleted) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        boolean currentEnabled = Boolean.TRUE.equals(user.isEnabled());
        boolean currentDeleted = Boolean.TRUE.equals(user.isDeleted());

        boolean targetEnabled = Boolean.TRUE.equals(newEnabled);
        boolean targetDeleted = Boolean.TRUE.equals(newDeleted);

        // ❌ Cannot enable a deleted user
        if (currentDeleted && targetEnabled) {
            throw new BusinessException(
                    "Deleted user cannot be enabled");
        }

        // ❌ Cannot mark enabled user as deleted
        if (targetDeleted && targetEnabled) {
            throw new BusinessException(
                    "User cannot be enabled and deleted at the same time");
        }

        // ❌ No-op protection (no actual change)
        if (currentEnabled == targetEnabled &&
            currentDeleted == targetDeleted) {

            throw new BusinessException(
                    "No status change detected");
        }

        // ❌ Cannot delete already deleted user
        if (currentDeleted && targetDeleted) {
            throw new BusinessException(
                    "User is already deleted");
        }

        // ❌ Cannot enable user without un-deleting first
        if (currentDeleted && !targetDeleted) {
            throw new BusinessException(
                    "Restore user before enabling");
        }
    }
}
