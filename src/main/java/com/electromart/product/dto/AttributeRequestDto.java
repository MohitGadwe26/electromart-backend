package com.electromart.product.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Data
public class AttributeRequestDto {

    private String name;
    private String dataType;
    private String unit;
    private Boolean filterable;
    private Long categoryId;
}

