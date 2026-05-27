package com.electromart.address.dto;

import com.electromart.address.entity.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;
    
    @NotBlank(message = "Street address is required")
    @Size(min = 5, max = 200, message = "Street address must be between 5 and 200 characters")
    private String street;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "^[0-9]{5,6}$", message = "Invalid ZIP code format")
    private String zipCode;
    
    private String landmark;
    
    private AddressType addressType;
    
    private boolean isDefault;
}
