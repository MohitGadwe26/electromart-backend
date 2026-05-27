package com.electromart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:YmFzZTY0c2VjcmV0a2V5Zm9yand0dG9rZW5nZW5lcmF0aW9uYW5kdmVyaWZpY2F0aW9u}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 1 day in milliseconds
    private long expiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days for refresh token
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 secret key. Please check your jwt.secret configuration.");
            throw new IllegalArgumentException("Invalid JWT secret key format. Must be Base64 encoded.", e);
        } catch (Exception e) {
            log.error("Failed to create signing key: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize JWT signing key", e);
        }
    }

    // Generate JWT Token
    public String generateToken(String email) {
        return generateToken(email, expiration);
    }

    // Generate Refresh Token
    public String generateRefreshToken(String email) {
        return generateToken(email, refreshExpiration);
    }

    private String generateToken(String email, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, expirationTime);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        try {
            return Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(getSigningKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to create JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    // Extract username from token (email in this case)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Alias for extractUsername (for backward compatibility)
    public String extractEmail(String token) {
        return extractUsername(token);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract any claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("JWT token expired", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new RuntimeException("JWT claims string is empty", e);
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    // Validate token with username
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.warn("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }

    // Get time until expiration in milliseconds
    public long getTimeUntilExpiration(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            long timeUntilExpiration = expiration.getTime() - now.getTime();
            return Math.max(timeUntilExpiration, 0);
        } catch (Exception e) {
            log.warn("Failed to get time until expiration: {}", e.getMessage());
            return 0;
        }
    }

    // Check if token will expire soon (e.g., within 5 minutes)
    public boolean isTokenExpiringSoon(String token, long thresholdMillis) {
        long timeUntilExpiration = getTimeUntilExpiration(token);
        return timeUntilExpiration > 0 && timeUntilExpiration <= thresholdMillis;
    }

    // Generate token with custom claims
    public String generateTokenWithClaims(String email, Map<String, Object> claims) {
        return createToken(claims, email, expiration);
    }

    // Extract all claims for debugging
    public Claims getAllClaims(String token) {
        return extractAllClaims(token);
    }

    // Extract specific claim
    public Object getClaim(String token, String claimName) {
        final Claims claims = extractAllClaims(token);
        return claims.get(claimName);
    }

    // Check if token needs refresh (expiring within threshold)
    public boolean needsRefresh(String token, long refreshThreshold) {
        return isTokenExpiringSoon(token, refreshThreshold);
    }
}