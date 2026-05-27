package com.electromart.product.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@Data
public class BrandResponseDto {
    private Long id;
    private String name;
    private String logoUrl;
    private Boolean active;
}
