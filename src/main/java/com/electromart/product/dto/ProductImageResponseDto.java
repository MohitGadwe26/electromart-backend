package com.electromart.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ProductImageResponseDto {
    private Long id;
    private String imageUrl;
    private Boolean primaryImage;
}

