package com.electromart.product.controller;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminProductFilterResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private Boolean active;

    private Long categoryId;
    private String categoryName;

    private Long brandId;
    private String brandName;

    private Map<String, String> attributes;
}

