package com.electromart.address.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressUpdateRequest {
    
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;
    
    @Size(min = 5, max = 200, message = "Street address must be between 5 and 200 characters")
    private String street;
    
    private String city;
    private String state;
    private String country;
    
    @Pattern(regexp = "^[0-9]{5,6}$", message = "Invalid ZIP code format")
    private String zipCode;
    
    private String landmark;
    private Boolean isDefault;
}