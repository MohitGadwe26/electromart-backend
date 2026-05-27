package com.electromart.address.dto;

//import com.electromart.address.entity.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.electromart.address.entity.AddressType;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String landmark;
    private AddressType addressType;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}