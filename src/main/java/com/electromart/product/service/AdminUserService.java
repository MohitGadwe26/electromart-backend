package com.electromart.product.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;
import com.electromart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    // 1️⃣ View all users
    public List<User> getAllUsers() {
        return userRepository.findByDeletedFalse();
    }

    // 2️⃣ Block / Unblock user
    public void updateUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(enabled);
        userRepository.save(user);
    }

    // 3️⃣ Assign role
    public void updateUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(Role.valueOf(role));
        userRepository.save(user);
    }

    // 4️⃣ Soft delete user
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDeleted(true);
        user.setEnabled(false);
        userRepository.save(user);
    }
}

