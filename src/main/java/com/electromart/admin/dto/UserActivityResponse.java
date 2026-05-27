package com.electromart.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityResponse {
    private String activityType; // LOGIN, ORDER, REVIEW, PROFILE_UPDATE, etc.
    private String description;
    private LocalDateTime timestamp;
    private String ipAddress; // Optional
    private String userAgent; // Optional
}