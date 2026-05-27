package com.electromart.product.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductListResponseDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private String brandName;
    private Long categoryId;
    private String categoryName;
}

