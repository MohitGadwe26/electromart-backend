// CategoryUpdateRequestDTO.java
package com.electromart.product.dto;

import lombok.Data;

@Data
public class CategoryUpdateRequestDTO {
    private String name;
    private String description;
    private String imageUrl;
    private String icon;
    private Long parentId;
    private Boolean active;
}