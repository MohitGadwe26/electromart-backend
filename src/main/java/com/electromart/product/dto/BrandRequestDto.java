package com.electromart.product.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Data
public class BrandRequestDto {
    private String name;
    private String logoUrl;
}
