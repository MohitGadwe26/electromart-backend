package com.electromart.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationResponse {
    
    private int total;
    private int successful;
    private int failed;
    private List<String> errors;
    private LocalDateTime timestamp;
    
    @Builder.Default
    private String message = "Bulk operation completed";
}