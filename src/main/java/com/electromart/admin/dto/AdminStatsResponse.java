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
public class AdminStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long verifiedUsers;
    private long recentRegistrations;
    private long userCount;
    private long adminCount;
    private LocalDateTime generatedAt;
} 