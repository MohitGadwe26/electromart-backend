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
public class AddressSummaryResponse {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private boolean isDefault;
    private LocalDateTime createdAt;
}