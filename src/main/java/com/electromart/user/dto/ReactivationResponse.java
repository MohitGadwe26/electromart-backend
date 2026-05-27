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
public class ReactivationResponse {
    private String email;
    private String reactivationToken;
    private String reactivationLink;
    private LocalDateTime expiresAt;
    private String message;
}