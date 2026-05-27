package com.electromart.product;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private int stock;
    private String sku;
    //@Builder.Default 
    private boolean active;

    // Add featured flag
    @Builder.Default
    private boolean featured = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "purchase_count")
    private Integer purchaseCount = 0;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "offer_active")
    private Boolean offerActive = false;

    @Column(name = "offer_end_date")
    private LocalDateTime offerEndDate;

    


 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, 
    orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductAttributeValue> attributeValues = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, 
    orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();  

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Add these to your Product entity
    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "offer_text")
    private String offerText;

        // Helper method to add image
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    // Helper method to remove image
    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }

    // Get primary image URL
    public String getPrimaryImageUrl() {
        return images.stream()
                .filter(ProductImage::isPrimaryImage)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }
}
