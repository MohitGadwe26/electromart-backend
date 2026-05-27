package com.electromart.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPreferencesRequest {
    private boolean emailNotifications;
    private boolean smsNotifications;
    private boolean newsletterSubscription;
    
    @NotBlank(message = "Preferred language is required")
    private String preferredLanguage;
    
    @NotBlank(message = "Currency is required")
    private String currency;
    
    private String theme; // light/dark/system
}