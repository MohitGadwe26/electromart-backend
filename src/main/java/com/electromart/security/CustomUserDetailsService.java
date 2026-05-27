package com.electromart.security;

import com.electromart.common.entity.User;
import com.electromart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        
        // Use findByEmailAndDeletedFalse to exclude deleted users
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
        
        // Check if user is enabled (email verified)
        if (!user.isEnabled()) {
            log.warn("User account not verified: {}", email);
            throw new UsernameNotFoundException("Please verify your email first");
        }
        
        log.debug("User loaded successfully: {}", email);
        
        // Using UserBuilder for better readability
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ))
                .disabled(user.isDeleted())        // Account deleted?
                .accountExpired(false)             // Account expired?
                .credentialsExpired(false)         // Credentials expired?
                .accountLocked(false)              // Account locked?
                .build();
    }
}