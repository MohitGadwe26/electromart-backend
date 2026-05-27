package com.electromart.product;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;
    
    private String imageUrl;  // ✅ Add this for category images
    
    private String icon;       // ✅ Add emoji icon
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Builder.Default
    private boolean active = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Add these fields to your existing Category entity
    @Column(name = "show_on_homepage")
    private boolean showOnHomepage = false;

    @Column(name = "homepage_display_order")
    private Integer homepageDisplayOrder = 0;

    @Column(name = "homepage_card_title")
    private String homepageCardTitle;

    @Column(name = "card_type")
    private String cardType = "PRODUCT_CARD";  // PRODUCT_CARD, RECENTLY_VIEWED, KEEP_SHOPPING, DEALS, PROMOTIONAL

 
    @Column(name = "homepage_product_limit")
    private Integer homepageProductLimit = 4;  // Default 4, can be any number
    
    @Column(name = "homepage_max_price")
    private BigDecimal homepageMaxPrice;
    
    @Column(name = "homepage_min_price")
    private BigDecimal homepageMinPrice;

    @Column(name = "homepage_redirect_url")
    private String homepageRedirectUrl;  // For promotional cards

    @Column(name = "featured_image_url")
    private String featuredImageUrl;  // For card background/banner
}

   