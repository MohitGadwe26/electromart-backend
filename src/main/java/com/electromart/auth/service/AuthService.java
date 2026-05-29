package com.electromart.auth.service;

import com.electromart.auth.dto.AuthResponse;
import com.electromart.auth.dto.LoginRequest;
import com.electromart.auth.dto.ResetPasswordRequest;
import com.electromart.auth.dto.SignupRequest;
import com.electromart.auth.exception.EmailAlreadyExistsException;
import com.electromart.auth.exception.InvalidCredentialsException;
import com.electromart.auth.exception.InvalidTokenException;
import com.electromart.auth.exception.UserNotFoundException;
import com.electromart.auth.exception.UserNotVerifiedException;
import com.electromart.auth.exception.ValidationException;
import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;
import com.electromart.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.electromart.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    
    @Value("${google.client.id}")
    private String googleClientId;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }
    
    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.USER)
                .enabled(false)  // MUST BE false until email verified
                .deleted(false)
                .emailVerificationToken(UUID.randomUUID().toString())
                .emailVerificationExpiry(LocalDateTime.now().plusDays(1))
                .build();
        
        userRepository.save(user);
        
        // Try to send verification email
        sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());
        
        log.info("User registered (pending verification): {}", request.getEmail());
    }
    
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));
        
        if (user.getEmailVerificationExpiry() == null || 
            user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Verification token expired");
        }
        
        user.setEnabled(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiry(null);
        userRepository.save(user);
        
        log.info("Email verified for user: {}", user.getEmail());
        
        // Send welcome email after verification
        sendWelcomeEmail(user.getEmail(), user.getName());
    }
    
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Check if already verified
        if (user.isEnabled()) {
            throw new ValidationException("Email is already verified");
        }
        
        // Check if verification token expired or null
        if (user.getEmailVerificationToken() == null || 
            user.getEmailVerificationExpiry() == null ||
            user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
            // Generate new token
            user.setEmailVerificationToken(UUID.randomUUID().toString());
            user.setEmailVerificationExpiry(LocalDateTime.now().plusDays(1));
            userRepository.save(user);
        }
        
        // Resend verification email
        sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());
        
        log.info("Verification email resent for user: {}", email);
    }
    
    private void sendVerificationEmail(String email, String token) {
        String verificationLink = "https://electromart-frontend-6xfk.onrender.com/verify-email?token=" + token;
        String subject = "Verify Your Email - ElectroMart";
        String body = "Hello,\n\nPlease verify your email by clicking the link below:\n\n" + 
                     verificationLink + "\n\nThis link expires in 24 hours.\n\n" +
                     "If you did not create an account, please ignore this email.\n\n" +
                     "Regards,\nElectroMart Team";
        
        if (emailService != null) {
            try {
                emailService.sendEmail(email, subject, body);
                log.info("Verification email sent to: {}", email);
            } catch (Exception e) {
                // Don't fail signup if email fails
                log.error("Failed to send verification email to {}: {}", email, e.getMessage());
                log.info("📧 [FALLBACK] Verification link for {}: {}", email, verificationLink);
            }
        } else {
            log.info("📧 [DEV MODE] Verification link for {}: {}", email, verificationLink);
        }
    }
    
    private void sendWelcomeEmail(String email, String name) {
        String subject = "Welcome to ElectroMart!";
        String body = "Hi " + name + ",\n\nWelcome to ElectroMart! Your email has been verified successfully.\n\n" +
                      "Start shopping now at http://localhost:5173\n\n" +
                      "Regards,\nElectroMart Team";
        
        if (emailService != null) {
            try {
                emailService.sendEmail(email, subject, body);
                log.info("Welcome email sent to: {}", email);
            } catch (Exception e) {
                log.warn("Failed to send welcome email to {}: {}", email, e.getMessage());
            }
        } else {
            log.info("📧 [DEV MODE] Welcome email would be sent to: {}", email);
        }
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        
        if (!user.isEnabled()) {
            throw new UserNotVerifiedException("Please verify your email first");
        }
        
        if (user.isDeleted()) {
            throw new UserNotFoundException("Account not found");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .name(user.getName())
                .email(user.getEmail())
                .userId(user.getId())
                .tokenExpiry(jwtUtil.extractExpiration(token))
                .refreshTokenExpiry(jwtUtil.extractExpiration(refreshToken))
                .build();   
    }

    @Transactional
    public AuthResponse googleLogin(String token) {
        try {
            log.info("Processing Google login with token");
            
            // Verify Google token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
            .setAudience(Collections.singletonList(googleClientId))
            .build();

            GoogleIdToken idToken = verifier.verify(token);

            if (idToken == null) {
                log.error("Invalid Google token: verification failed");
                throw new InvalidTokenException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            // Get user details from Google
            String email = payload.getEmail();
            String name = (String) payload.get("name");
           // String picture = (String) payload.get("picture");
            boolean emailVerified = payload.getEmailVerified();

            if (!emailVerified) {
                log.error("Google email not verified for: {}", email);
                throw new ValidationException("Email not verified by Google");
            }

            log.info("Google login successful for email: {}, name: {}", email, name);

            // Check if user exists
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // Create new user for Google login
                user = User.builder()
                        .name(name)
                        .email(email)
                        .password("") // Google users don't need password
                        .phone("") // Empty phone for Google users
                        .role(Role.USER)
                        .enabled(true) // Already verified by Google
                        .deleted(false)
                        .build();

                userRepository.save(user);
                log.info("New user created via Google login: {}", email);
                
                // Send welcome email for new Google users
                sendWelcomeEmail(user.getEmail(), user.getName());
            } else {
                // Existing user - ensure they're enabled
                if (!user.isEnabled()) {
                    log.info("Enabling previously disabled user from Google login: {}", email);
                    user.setEnabled(true);
                    userRepository.save(user);
                }
                log.info("Existing user logged in via Google: {}", email);
            }

            // Generate JWT tokens
            String jwt = jwtUtil.generateToken(user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            return AuthResponse.builder()
                    .token(jwt)
                    .refreshToken(refreshToken)
                    .role(user.getRole().name())
                    .name(user.getName())
                    .email(user.getEmail())
                    .userId(user.getId())
                    .tokenExpiry(jwtUtil.extractExpiration(jwt))
                    .refreshTokenExpiry(jwtUtil.extractExpiration(refreshToken))
                    .build();

        } catch (InvalidTokenException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google login failed", e);
            throw new RuntimeException("Google login failed: " + e.getMessage(), e);
        }
    }
     
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        
        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        String newToken = jwtUtil.generateToken(user.getEmail());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .role(user.getRole().name())
                .name(user.getName())
                .email(user.getEmail())
                .userId(user.getId())
                .tokenExpiry(jwtUtil.extractExpiration(newToken))
                .refreshTokenExpiry(jwtUtil.extractExpiration(newRefreshToken))
                .build();
    }
    
    @Transactional
    public void initiatePasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmailAndDeletedFalse(email);
        
        // Security: Don't reveal if user exists or not
        userOptional.ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
            
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetTokenExpiry(expiryTime);
            userRepository.save(user);
            
            sendPasswordResetEmail(user.getEmail(), resetToken);
            
            log.info("Password reset initiated for user: {}", user.getEmail());
        });
    }
    
    private void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;
        String subject = "Password Reset Request - ElectroMart";
        String body = "Hello,\n\nWe received a request to reset your password.\n\n" +
                     "Click the link below to reset your password:\n" + 
                     resetLink + "\n\n" +
                     "Or use this token: " + resetToken + "\n\n" +
                     "This link expires in 15 minutes.\n\n" +
                     "If you didn't request this, please ignore this email.\n\n" +
                     "Regards,\nElectroMart Team";
        
        if (emailService != null) {
            try {
                emailService.sendEmail(toEmail, subject, body);
                log.info("Password reset email sent to: {}", toEmail);
            } catch (Exception e) {
                log.error("Failed to send password reset email: {}", e.getMessage());
                log.info("📧 [FALLBACK] Reset token for {}: {}", toEmail, resetToken);
                log.info("📧 [FALLBACK] Reset link: {}", resetLink);
            }
        } else {
            log.info("📧 [DEV MODE] Password reset token for {}: {}", toEmail, resetToken);
            log.info("📧 [DEV MODE] Reset link: {}", resetLink);
        }
    }
    
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        User user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));
        
        if (user.getPasswordResetTokenExpiry() == null || 
            user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        
        log.info("Password reset successful for user: {}", user.getEmail());
        
        sendPasswordResetConfirmationEmail(user.getEmail());
    }
    
    private void sendPasswordResetConfirmationEmail(String toEmail) {
        String subject = "Password Reset Successful - ElectroMart";
        String body = "Hello,\n\nYour password has been reset successfully.\n\n" +
                     "If you did not perform this action, please contact support immediately.\n\n" +
                     "Regards,\nElectroMart Team";
        
        if (emailService != null) {
            try {
                emailService.sendEmail(toEmail, subject, body);
                log.info("Password reset confirmation sent to: {}", toEmail);
            } catch (Exception e) {
                log.warn("Failed to send confirmation email: {}", e.getMessage());
            }
        } else {
            log.info("📧 [DEV MODE] Password reset confirmed for: {}", toEmail);
        }
    }
    
    public boolean validatePasswordResetToken(String token) {
        Optional<User> userOptional = userRepository.findByPasswordResetToken(token);
        
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        return user.getPasswordResetTokenExpiry() != null 
                && user.getPasswordResetTokenExpiry().isAfter(LocalDateTime.now());
    }
}