package com.electromart.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AttributeResponseDto {

    private Long id;
    private String name;
    private String dataType;
    private String unit;
    private Boolean filterable;
    private Boolean active;
    private Long categoryId;
}

