package com.electromart.product.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Data
public class ProductAttributeValueRequestDto {
    private Long productId;
    private Long attributeId;
    private String value;
}

