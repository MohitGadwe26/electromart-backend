// BulkStatusUpdateRequest.java
package com.electromart.admin.dto;

import lombok.Data;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkStatusUpdateRequest {
    
    @NotNull(message = "User IDs cannot be null")
    @Size(min = 1, message = "At least one user ID is required")
    private List<Long> userIds;
    
    private Boolean enabled;
    
    private Boolean deleted;
    
    private String reason;
    
    private Boolean notifyUsers;
}