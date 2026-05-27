package com.electromart.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ProductAttributeValueResponseDto {
    private Long id;
    private Long productId;
    private Long attributeId;
    private String attributeName;
    private String value;
}

