package com.electromart.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.electromart.product.dto.HomepageCardDto;
import com.electromart.product.service.HomepageService;
import com.electromart.user.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/homepage")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class HomepageController {

    private final HomepageService homepageService;
    private final UserRepository userRepository;

    @GetMapping("/cards")
    public ResponseEntity<List<HomepageCardDto>> getHomepageCards() {
        // Get current logged-in user ID (if logged in)
        Long userId = getCurrentUserId();
        
        log.info("Fetching homepage cards for user: {}", userId);
        List<HomepageCardDto> cards = homepageService.getHomepageCards(userId);
        return ResponseEntity.ok(cards);
    }
    
    /**
     * Get current user ID from Security Context
     * Returns null if user is not logged in
     */
    private Long getCurrentUserId() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (email != null && !email.equals("anonymousUser")) {
                return userRepository.findByEmail(email)
                    .map(com.electromart.common.entity.User::getId)
                    .orElse(null);
            }
        } catch (Exception e) {
            log.debug("No authenticated user found");
        }
        return null;
    }
}