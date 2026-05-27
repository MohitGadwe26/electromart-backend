package com.electromart.admin.service.audit;

import java.util.List;

import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;

public interface  AuditService {
 void logUserStatusChange(User admin,
                             User target,
                             String reason);

    void logBulkStatusChange(User admin,
                             List<Long> userIds,
                             String reason);

                             public void logUserRoleChange(User admin,
                                  User user,
                                  Role oldRole,
                                  Role newRole);

                                   public void logUserRestore(User admin,
                               User user);
        public void logUserDeletion(User admin,
                                User user);

                             
}
