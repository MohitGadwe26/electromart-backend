package com.electromart.product.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private Boolean active;
    private Boolean featured;
    private String imageUrl;

    private Long categoryId;
    private String categoryName;

    private Long brandId;
    private String brandName;

    // Add these fields
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private String offerText;
}
