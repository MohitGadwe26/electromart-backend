package com.electromart.product.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class ProductFilterRequest {
    private Long categoryId;
    private String brand;           // Brand name
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy;          // Add sort field
    private String gender;          // Add gender filter
    private String productType;     // Add product type filter
    private String sleeveType;      // Add sleeve type filter
    private String pattern;         // Add pattern filter
    private Boolean featured;       // Featured products only
    private Map<String, String> attributes;  // Existing attribute filters
}