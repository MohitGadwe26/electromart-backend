// CategoryResponseDto.java - Add missing fields
package com.electromart.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;  // ✅ Add this
    private String icon;       // ✅ Add this
    private Long parentId;
    private boolean active;
    private Long productCount; // ✅ Add product count
    private String cardType;
}