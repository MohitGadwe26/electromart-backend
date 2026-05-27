package com.electromart.product.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryFilterResponse {

    private boolean price;
    private boolean brand;
    private List<AttributeFilterDto> attributes;
}

