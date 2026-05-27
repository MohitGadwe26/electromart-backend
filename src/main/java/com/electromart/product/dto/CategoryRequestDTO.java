// CategoryRequestDTO.java
package com.electromart.product.dto;

import lombok.Data;

@Data
public class CategoryRequestDTO {
    private String name;
    private String description;
    private String imageUrl;
    private String icon;
    private Long parentId;
}