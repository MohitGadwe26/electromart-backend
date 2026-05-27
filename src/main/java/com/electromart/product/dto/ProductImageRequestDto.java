package com.electromart.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Builder
public class ProductImageRequestDto {
    private Long productId;
    private String imageUrl;
    private Boolean primaryImage;
}

