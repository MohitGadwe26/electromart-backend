package com.electromart.common.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
@Setter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private T data;
}

