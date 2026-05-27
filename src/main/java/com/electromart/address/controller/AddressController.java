package com.electromart.address.controller;

import com.electromart.address.dto.AddressRequest;
import com.electromart.address.dto.AddressResponse;
import com.electromart.address.dto.AddressUpdateRequest;
import com.electromart.address.service.AddressService;
import com.electromart.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class AddressController {
    
    private final AddressService addressService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
        @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.createAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AddressResponse>builder()
                        .success(true)
                        .message("Address created successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAllAddresses() {
        List<AddressResponse> addresses = addressService.getAllUserAddresses();
        return ResponseEntity.ok(
                ApiResponse.<List<AddressResponse>>builder()
                        .success(true)
                        .message("Addresses retrieved successfully")
                        .timestamp(LocalDateTime.now())
                        .data(addresses)
                        .build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
        @PathVariable Long id) {
        AddressResponse response = addressService.getAddressById(id);
        return ResponseEntity.ok(
                ApiResponse.<AddressResponse>builder()
                        .success(true)
                        .message("Address retrieved successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressUpdateRequest request) {
        AddressResponse response = addressService.updateAddress(id, request);
        return ResponseEntity.ok(
                ApiResponse.<AddressResponse>builder()
                        .success(true)
                        .message("Address updated successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Address deleted successfully")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
    
    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable Long id) {
        AddressResponse response = addressService.setDefaultAddress(id);
        return ResponseEntity.ok(
                ApiResponse.<AddressResponse>builder()
                        .success(true)
                        .message("Default address set successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @GetMapping("/default")
    public ResponseEntity<ApiResponse<AddressResponse>> getDefaultAddress() {
        AddressResponse response = addressService.getDefaultAddress();
        return ResponseEntity.ok(
                ApiResponse.<AddressResponse>builder()
                        .success(true)
                        .message("Default address retrieved successfully")
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build());
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getAddressCount() {
        long count = addressService.getAddressCount();
        return ResponseEntity.ok(
                ApiResponse.<Long>builder()
                        .success(true)
                        .message("Address count retrieved")
                        .timestamp(LocalDateTime.now())
                        .data(count)
                        .build());
    }
}