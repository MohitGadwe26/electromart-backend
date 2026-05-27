package com.electromart.content.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;
    
    private String title;
    private String subtitle;
    private String description;
    private String discountText;
    private String badgeText;

    // Filter configuration
    private Long categoryId;
    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy;
    private String gender;
    private String productType;
    private String sleeveType;
    private String pattern;
    private Boolean featured;

    @Builder.Default
    private boolean active = true;
    
    private Integer displayOrder;

    @CreationTimestamp
    private LocalDateTime createdAt;
}