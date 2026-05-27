package com.electromart.admin.dto;

import com.electromart.common.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserDetailResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean enabled;
    private boolean deleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int addressCount;
    private List<AddressSummary> addresses;

    private int orderCount;
    private int reviewCount;
    private double totalSpent;

    /* ===================== INNER DTO ===================== */

    @Data
    @Builder
    public static class AddressSummary {
        private Long id;
        private String street;
        private String city;
        private String state;
        private String country;
        private boolean isDefault;
        private LocalDateTime createdAt;
    }
}
