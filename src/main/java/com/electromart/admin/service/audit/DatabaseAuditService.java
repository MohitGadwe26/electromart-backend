package com.electromart.admin.service.audit;

import java.util.List;

import org.springframework.stereotype.Service;

import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatabaseAuditService implements AuditService {
   /* =========================================================
       USER STATUS
       ========================================================= */

    // public void logUserStatusChange(User admin,
    //                                 User user,
    //                                 String reason) {

    //     log.info(
    //         "AUDIT | STATUS_CHANGE | adminId={} | userId={} | enabled={} | deleted={} | reason={}",
    //         admin.getId(),
    //         user.getId(),
    //         user.isEnabled(),
    //         user.isDeleted(),
    //         reason
    //     );
    // }

    /* =========================================================
       ROLE CHANGE
       ========================================================= */

    // public void logUserRoleChange(User admin,
    //                               User user,
    //                               Role oldRole,
    //                               Role newRole) {

    //     log.info(
    //         "AUDIT | ROLE_CHANGE | adminId={} | userId={} | from={} | to={}",
    //         admin.getId(),
    //         user.getId(),
    //         oldRole,
    //         newRole
    //     );
    // }

    /* =========================================================
       RESTORE
       ========================================================= */

    // public void logUserRestore(User admin, User user) {

    //     log.info(
    //         "AUDIT | USER_RESTORE | adminId={} | userId={}",
    //         admin.getId(),
    //         user.getId()
    //     );
    // }

    /* =========================================================
       DELETION
       ========================================================= */

    // public void logUserDeletion(User admin, User user) {

    //     log.info(
    //         "AUDIT | USER_DELETION | adminId={} | userId={}",
    //         admin.getId(),
    //         user.getId()
    //     );
    // }

    @Override
    public void logBulkStatusChange(User admin, List<Long> userIds, String reason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logBulkStatusChange'");
    }


    //     public void logUserStatusChange(User admin,
    //                                 User user,
    //                                 String reason) {

    //     log.info(
    //         "[AUDIT] Admin {} changed status of User {} | enabled={}, deleted={} | reason={}",
    //         admin.getId(),
    //         user.getId(),
    //         user.isEnabled(),
    //         user.isDeleted(),
    //         reason
    //     );
    // }

    /* ================= USER ROLE ================= */

    // public void logUserRoleChange(User admin,
    //                               User user,
    //                               Role oldRole,
    //                               Role newRole) {

    //     log.info(
    //         "[AUDIT] Admin {} changed role of User {} | {} -> {}",
    //         admin.getId(),
    //         user.getId(),
    //         oldRole,
    //         newRole
    //     );
    // }

    /* ================= USER RESTORE ================= */

    // public void logUserRestore(User admin,
    //                            User user) {

    //     log.info(
    //         "[AUDIT] Admin {} restored User {}",
    //         admin.getId(),
    //         user.getId()
    //     );
    // }

    /* ================= USER DELETION ================= */

    // public void logUserDeletion(User admin,
    //                             User user) {

    //     log.info(
    //         "[AUDIT] Admin {} deleted User {}",
    //         admin.getId(),
    //         user.getId()
    //     );
    // }

      /* ================= USER STATUS ================= */

    public void logUserStatusChange(User admin,
                                    User user,
                                    String reason) {

        // Persist audit entry / send to logs / Kafka / DB
        // Example:
        System.out.println(
                "[AUDIT] Admin " + admin.getId()
                + " changed status of User " + user.getId()
                + " | Reason: " + reason
        );
    }

    /* ================= USER ROLE ================= */

    public void logUserRoleChange(User admin,
                                  User user,
                                  Role oldRole,
                                  Role newRole) {

        System.out.println(
                "[AUDIT] Admin " + admin.getId()
                + " changed role of User " + user.getId()
                + " from " + oldRole
                + " to " + newRole
        );
    }

    /* ================= USER RESTORE ================= */

    public void logUserRestore(User admin,
                               User user) {

        System.out.println(
                "[AUDIT] Admin " + admin.getId()
                + " restored User " + user.getId()
        );
    }

    /* ================= USER DELETION ================= */

    public void logUserDeletion(User admin,
                                User user) {

        System.out.println(
                "[AUDIT] Admin " + admin.getId()
                + " deleted User " + user.getId()
        );
    }

}
