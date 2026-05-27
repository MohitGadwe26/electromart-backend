package com.electromart.auth.service;

import com.electromart.common.entity.User;
import com.electromart.auth.exception.InvalidTokenException;
import com.electromart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenService {
    
    private final UserRepository userRepository;
    
    public String createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElse(null);
        
        if (user == null) {
            log.warn("Password reset requested for non-existent email: {}", email);
            return null; // Return null for security (don't reveal user existence)
        }
        
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
        
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(expiryTime);
        userRepository.save(user);
        
        log.info("Password reset token created for user: {}", email);
        return token;
    }
    
    public boolean validatePasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token)
                .map(user -> 
                    user.getPasswordResetTokenExpiry() != null 
                    && user.getPasswordResetTokenExpiry().isAfter(LocalDateTime.now())
                )
                .orElse(false);
    }
    
    public User getUserByPasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token)
                .filter(user -> 
                    user.getPasswordResetTokenExpiry() != null 
                    && user.getPasswordResetTokenExpiry().isAfter(LocalDateTime.now())
                )
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));
    }
    
    public void invalidatePasswordResetToken(User user) {
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        log.info("Password reset token invalidated for user: {}", user.getEmail());
    }
}