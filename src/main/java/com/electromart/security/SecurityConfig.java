package com.electromart.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ===== PUBLIC ENDPOINTS =====
                        .requestMatchers(
                                // Authentication endpoints
                                "/api/auth/signup",
                                "/api/auth/login", 
                                "/api/auth/google",
                                "/api/auth/refresh-token",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/verify-email/**",
                                "/api/auth/validate-reset-token",
                                "/api/auth/resend-verification",

                                // ===== HOMEPAGE ENDPOINTS (PUBLIC ACCESS) =====
                                "/api/homepage/cards",
                                "/api/homepage/**",

                                // ===== BANNER ENDPOINTS (PUBLIC ACCESS) =====
                                "/api/banners/active",
                                "/api/banners/public/**",
                                
                                // Public product/category endpoints
                                "/api/products",
                                "/api/products/{id}",
                                "/api/products/search/**",
                                "/api/products/category/**",
                                "/api/categories",
                                "/api/categories/{id}",
                                "/api/users/profile/reactivate",
                                "/api/users/profile/request-reactivation",
                                
                                // ===== STATIC RESOURCES (IMAGES) =====
                                "/uploads/**",
                                "/images/**",
                                "/static/**",
                                "/resources/**",
                                
                                // ===== SWAGGER UI & API DOCS =====
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/v3/api-docs.yaml",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/webjars/**"
                        ).permitAll()
                        
                        // ===== ADMIN ENDPOINTS =====
                        .requestMatchers(
                                "/api/admin/users/**",
                                "/api/admin/**",
                                "/api/products/create",
                                "/api/products/update/**", 
                                "/api/products/delete/**",
                                "/api/categories/create",
                                "/api/categories/update/**",
                                "/api/categories/delete/**",
                                "/api/banners/**"  // Admin CRUD operations on banners
                        ).hasRole("ADMIN")
                        
                        // ===== USER ENDPOINTS =====
                        .requestMatchers(
                                "/api/users/**",
                                "/api/profile/**",
                                "/api/addresses/**",
                                "/api/cart/**",
                                "/api/orders/**",
                                "/api/wishlist/**",
                                "/api/payments/**",
                                "/api/reviews/**"
                        ).hasAnyRole("USER", "ADMIN")
                        
                        // ===== DEFAULT - ALL OTHER ENDPOINTS =====
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:5173", 
            "http://localhost:5174", 
            "http://localhost:3000",
            "http://localhost:8080"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}