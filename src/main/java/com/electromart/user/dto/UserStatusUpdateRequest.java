package com.electromart.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateRequest {
    
    private Boolean enabled;
    
    private Boolean deleted;
    
    private String reason;
    
    @Builder.Default
    private Boolean notifyUsers = false;
    
    // Validation methods
    public boolean hasChanges() {
        return enabled != null || deleted != null;
    }
    
    public void validate() {
        if (!hasChanges()) {
            throw new IllegalArgumentException("At least one status field must be provided");
        }
        
        // Cannot enable a deleted user in single operation
        if (Boolean.TRUE.equals(enabled) && Boolean.TRUE.equals(deleted)) {
            throw new IllegalArgumentException("Cannot enable and delete a user simultaneously");
        }
    }
}