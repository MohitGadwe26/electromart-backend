package com.electromart.admin.dto;

import com.electromart.common.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    @NotNull(message = "Role is required")
    private Role role;
}