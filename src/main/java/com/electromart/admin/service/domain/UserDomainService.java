package com.electromart.admin.service.domain;

import com.electromart.admin.dto.AdminStatsResponse;
import com.electromart.admin.dto.UserActivityResponse;
import com.electromart.admin.dto.UserDetailResponse;
import com.electromart.address.repository.AddressRepository;
import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.user.dto.UserResponse;
import com.electromart.user.exception.UserNotFoundException;
import com.electromart.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    /* ===================== AUTH CONTEXT ===================== */

    public User getCurrentAdmin() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("Admin user not found"));
    }

    /* ===================== BASIC FETCH ===================== */

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));
    }

        public List<User> findAllByIds(List<Long> userIds) {

        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("User IDs list cannot be empty");
        }

        List<User> users = userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            throw new ResourceNotFoundException(
                    "One or more users not found for provided IDs");
        }

        return users;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    /* ===================== LISTING ===================== */

    public Page<UserResponse> getAllUsers(int page, int size,
                                          String sortBy, String direction) {

        Sort.Direction dir = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        return userRepository.findByDeletedFalse(pageable)
                .map(this::toUserResponse);
    }

    public Page<UserResponse> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return userRepository.searchUsers(query, pageable)
                .map(this::toUserResponse);
    }

    public Page<UserResponse> getUsersByRole(Role role, int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return userRepository.findByRoleAndDeletedFalse(role, pageable)
                .map(this::toUserResponse);
    }

    public Page<UserResponse> getUsersByStatus(boolean enabled, int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return userRepository.findByEnabledAndDeletedFalse(enabled, pageable)
                .map(this::toUserResponse);
    }

    /* ===================== SINGLE USER ===================== */

    public UserResponse getUserResponse(Long userId) {
        return toUserResponse(getUser(userId));
    }

    public UserDetailResponse getUserDetail(Long userId) {
        User user = getUser(userId);

        int addressCount = addressRepository
                .findByUserAndDeletedFalse(user).size();

        return UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .deleted(user.isDeleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .addressCount(addressCount)
                .addresses(addressRepository.findByUserAndDeletedFalse(user)
                        .stream()
                        .map(a -> UserDetailResponse.AddressSummary.builder()
                                .id(a.getId())
                                .street(a.getStreet())
                                .city(a.getCity())
                                .state(a.getState())
                                .country(a.getCountry())
                                .isDefault(a.isDefault())
                                .createdAt(a.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .orderCount(0)
                .reviewCount(0)
                .totalSpent(0.0)
                .build();
    }

    /* ===================== ACTIVITY ===================== */

    public List<UserActivityResponse> getUserActivity(Long userId) {
        getUser(userId); // validate existence

        return List.of(
                UserActivityResponse.builder()
                        .activityType("LOGIN")
                        .description("User logged in")
                        .timestamp(LocalDateTime.now().minusHours(2))
                        .build(),
                UserActivityResponse.builder()
                        .activityType("PROFILE_UPDATE")
                        .description("Updated profile")
                        .timestamp(LocalDateTime.now().minusDays(1))
                        .build()
        );
    }

    /* ===================== DASHBOARD ===================== */

    public AdminStatsResponse getAdminStats() {

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();

        long verifiedUsers = userRepository
                .findByEnabledAndDeletedFalse(true,
                        PageRequest.of(0, Integer.MAX_VALUE))
                .getTotalElements();

        long userCount = userRepository
                .findByRoleAndDeletedFalse(Role.USER,
                        PageRequest.of(0, Integer.MAX_VALUE))
                .getTotalElements();

        long adminCount = userRepository
                .findByRoleAndDeletedFalse(Role.ADMIN,
                        PageRequest.of(0, Integer.MAX_VALUE))
                .getTotalElements();

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .verifiedUsers(verifiedUsers)
                .userCount(userCount)
                .adminCount(adminCount)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public List<UserResponse> getRecentRegistrations(int limit) {
        return userRepository.findAll().stream()
                .filter(u -> !u.isDeleted())
                .sorted((a, b) ->
                        b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    /* ===================== DELETE ===================== */

    public void softDelete(User user, Long adminId) {
        user.softDelete(adminId);
        save(user);
    }

    /* ===================== MAPPER ===================== */

    public UserResponse toUserResponse(User user) {
        int addressCount =
                addressRepository.findByUserAndDeletedFalse(user).size();

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .deleted(user.isDeleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .addressCount(addressCount)
                .build();
    }
}
