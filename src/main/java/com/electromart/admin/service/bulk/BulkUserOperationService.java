package com.electromart.admin.service.bulk;

import java.util.List;

import org.springframework.stereotype.Service;

import com.electromart.admin.dto.BulkStatusUpdateRequest;
import com.electromart.admin.service.audit.AuditService;
import com.electromart.admin.service.domain.UserDomainService;
import com.electromart.admin.service.policy.UserRolePolicy;
import com.electromart.admin.service.policy.UserStatusPolicy;
import com.electromart.common.entity.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BulkUserOperationService {

    private final UserDomainService domainService;
    private final UserRolePolicy rolePolicy;
    private final UserStatusPolicy statusPolicy;
    private final AuditService auditService;

    public void bulkUpdateStatus(BulkStatusUpdateRequest request) {

        User admin = domainService.getCurrentAdmin();
        List<User> users = domainService.findAllByIds(request.getUserIds());

        for (User user : users) {

            rolePolicy.validateAdminOperation(admin, user);
            statusPolicy.validateStatusChange(
                    user,
                    request.getEnabled(),
                    request.getDeleted()
            );

            user.updateStatusChange(
                    admin.getId(),
                    request.getEnabled(),
                    request.getDeleted()
            );

            domainService.save(user);
        }

        auditService.logBulkStatusChange(
                admin,
                request.getUserIds(),
                request.getReason()
        );
    }
}

