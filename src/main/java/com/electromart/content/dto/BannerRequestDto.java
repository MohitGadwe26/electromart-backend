package com.electromart.content.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class BannerRequestDto {
    private String imageUrl;
    private String title;
    private String subtitle;
    private String description;
    private String discountText;
    private String badgeText;
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
    private Integer displayOrder;
}