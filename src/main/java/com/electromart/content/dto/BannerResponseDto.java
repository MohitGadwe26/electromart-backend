package com.electromart.content.dto;

import lombok.Builder;
import lombok.Data;
import com.electromart.product.dto.ProductFilterRequest;

@Data
@Builder
public class BannerResponseDto {
    private Long id;
    private String imageUrl;
    private String title;
    private String subtitle;
    private String description;
    private String discountText;
    private String badgeText;
    private ProductFilterRequest filterRequest;
    private String redirectUrl;
    private Integer displayOrder;
    private boolean active;
}