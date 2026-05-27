package com.electromart.address.service;

import com.electromart.address.dto.AddressRequest;
import com.electromart.address.dto.AddressResponse;
import com.electromart.address.dto.AddressUpdateRequest;
import com.electromart.address.exception.AddressNotFoundException;
import com.electromart.address.exception.AddressOperationException;
import com.electromart.address.repository.AddressRepository;
import com.electromart.common.entity.User;
import com.electromart.address.entity.Address;
import com.electromart.user.repository.UserRepository;
import com.electromart.auth.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        User user = getCurrentUser();
        
        // If this is set as default, unset other default addresses
        if (request.isDefault()) {
            addressRepository.unsetDefaultAddress(user);
        }
        
        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .landmark(request.getLandmark())
                .addressType(request.getAddressType())
                .isDefault(request.isDefault())
                .deleted(false)
                .build();
        
        Address savedAddress = addressRepository.save(address);
        log.info("Address created for user {}: {}", user.getEmail(), savedAddress.getId());
        
        return convertToResponse(savedAddress);
    }
    
    @Transactional(readOnly = true)
    public List<AddressResponse> getAllUserAddresses() {
        User user = getCurrentUser();
        List<Address> addresses = addressRepository.findAllActiveByUser(user);
        
        return addresses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long addressId) {
        User user = getCurrentUser();
        Address address = addressRepository.findByIdAndUserAndDeletedFalse(addressId, user)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        
        return convertToResponse(address);
    }
    
    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressUpdateRequest request) {
        User user = getCurrentUser();
        Address address = addressRepository.findByIdAndUserAndDeletedFalse(addressId, user)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        
        // Update fields if provided
        if (request.getFullName() != null) address.setFullName(request.getFullName());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getStreet() != null) address.setStreet(request.getStreet());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getZipCode() != null) address.setZipCode(request.getZipCode());
        if (request.getLandmark() != null) address.setLandmark(request.getLandmark());
        
        // Handle default address update
        if (request.getIsDefault() != null && request.getIsDefault()) {
            if (!address.isDefault()) {
                addressRepository.unsetDefaultAddress(user);
                address.setDefault(true);
            }
        }
        
        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated: {}", addressId);
        
        return convertToResponse(updatedAddress);
    }
    
    @Transactional
    public void deleteAddress(Long addressId) {
        User user = getCurrentUser();
        Address address = addressRepository.findByIdAndUserAndDeletedFalse(addressId, user)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        
        // Check if this is the default address
        if (address.isDefault()) {
            throw new AddressOperationException("Cannot delete default address. Set another address as default first.");
        }
        
        address.setDeleted(true);
        addressRepository.save(address);
        log.info("Address soft deleted: {}", addressId);
    }
    
    @Transactional
    public AddressResponse setDefaultAddress(Long addressId) {
        User user = getCurrentUser();
        
        // Find the address to set as default
        Address newDefault = addressRepository.findByIdAndUserAndDeletedFalse(addressId, user)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        
        // Unset current default address
        addressRepository.unsetDefaultAddress(user);
        
        // Set new default
        newDefault.setDefault(true);
        Address savedAddress = addressRepository.save(newDefault);
        
        log.info("Default address set for user {}: {}", user.getEmail(), addressId);
        return convertToResponse(savedAddress);
    }
    
    @Transactional(readOnly = true)
    public AddressResponse getDefaultAddress() {
        User user = getCurrentUser();
        Address defaultAddress = addressRepository.findByUserAndIsDefaultTrueAndDeletedFalse(user)
                .orElseThrow(() -> new AddressNotFoundException("No default address found"));
        
        return convertToResponse(defaultAddress);
    }
    
    @Transactional(readOnly = true)
    public long getAddressCount() {
        User user = getCurrentUser();
        return addressRepository.countByUser(user);
    }
    
    private AddressResponse convertToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .landmark(address.getLandmark())
                .addressType(address.getAddressType())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}