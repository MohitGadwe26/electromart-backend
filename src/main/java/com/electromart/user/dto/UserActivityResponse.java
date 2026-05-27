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
public class UserActivityResponse {
    private Long userId;
    private LocalDateTime lastLogin;
    private int totalOrders;
    private int totalReviews;
    private int totalAddresses;
    private LocalDateTime profileUpdatedAt;
    // Can add more fields: wishlist items, cart items, etc.
}