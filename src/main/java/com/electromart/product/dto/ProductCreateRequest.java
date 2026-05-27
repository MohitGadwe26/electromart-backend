package com.electromart.product.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

  private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private Long categoryId;
    private Long brandId;
    private Boolean featured;
}
