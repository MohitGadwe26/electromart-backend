package com.electromart.admin.service.notification;

import org.springframework.stereotype.Service;

import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailNotificationService implements NotificationService {


    // public void notifyStatusChange(User user) {
    //     log.info("Sending status update email to {}", user.getEmail());
    // }

    
    /* =========================================================
       STATUS CHANGE
       ========================================================= */
    // @Override
    // public void notifyStatusChange(User user) {
    //     log.info(
    //         "NOTIFY | STATUS_CHANGE | userId={} | enabled={} | deleted={}",
    //         user.getId(),
    //         user.isEnabled(),
    //         user.isDeleted()
    //     );
    // }

    /* =========================================================
       ROLE CHANGE
       ========================================================= */

    // public void notifyRoleChange(User user, Role oldRole) {
    //     log.info(
    //         "NOTIFY | ROLE_CHANGE | userId={} | from={} | to={}",
    //         user.getId(),
    //         oldRole,
    //         user.getRole()
    //     );
    // }

    /* =========================================================
       RESTORE
       ========================================================= */

    public void notifyRestore(User user) {
        log.info(
            "NOTIFY | USER_RESTORE | userId={}",
            user.getId()
        );
    }

    /* =========================================================
       DELETION
       ========================================================= */

    public void notifyDeletion(User user) {
        log.info(
            "NOTIFY | USER_DELETION | userId={}",
            user.getId()
        );
    }

        public void notifyStatusChange(User user) {
        log.info(
            "[NOTIFY] User {} notified about status change | enabled={}, deleted={}",
            user.getId(),
            user.isEnabled(),
            user.isDeleted()
        );
    }

    /* ================= USER ROLE ================= */

    public void notifyRoleChange(User user, Role oldRole) {
        log.info(
            "[NOTIFY] User {} notified about role change | {} -> {}",
            user.getId(),
            oldRole,
            user.getRole()
        );
    }

    //     public void logUserStatusChange(User admin,
    //                                 User user,
    //                                 String reason) {

    //     // Persist audit entry / send to logs / Kafka / DB
    //     // Example:
    //     System.out.println(
    //             "[AUDIT] Admin " + admin.getId()
    //             + " changed status of User " + user.getId()
    //             + " | Reason: " + reason
    //     );
    // }

    //   /* ================= USER ROLE ================= */

    // public void logUserRoleChange(User admin,
    //                               User user,
    //                               Role oldRole,
    //                               Role newRole) {

    //     System.out.println(
    //             "[AUDIT] Admin " + admin.getId()
    //             + " changed role of User " + user.getId()
    //             + " from " + oldRole
    //             + " to " + newRole
    //     );
    // }

    //   /* ================= USER RESTORE ================= */

    // public void logUserRestore(User admin,
    //                            User user) {

    //     System.out.println(
    //             "[AUDIT] Admin " + admin.getId()
    //             + " restored User " + user.getId()
    //     );
    // }
    }


