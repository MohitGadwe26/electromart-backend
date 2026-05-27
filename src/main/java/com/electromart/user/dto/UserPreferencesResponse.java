package com.electromart.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesResponse {
    private Long userId;
    private boolean emailNotifications;
    private boolean smsNotifications;
    private boolean newsletterSubscription;
    private String preferredLanguage;
    private String currency;
    private String theme;
    private LocalDateTime updatedAt;
}