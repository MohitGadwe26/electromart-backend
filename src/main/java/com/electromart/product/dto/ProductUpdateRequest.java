package com.electromart.product.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;   
    private String brand;
    private String imageUrl;
    private Long categoryId; 
     private Boolean featured;
}
