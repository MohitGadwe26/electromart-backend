package com.electromart.product.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class AttributeFilterDto {
    private Long id;
    private String name;
    private String dataType;
    private String unit;
}

