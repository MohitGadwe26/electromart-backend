package com.electromart.product.dto;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomepageCardItemDto {
private Long id;
    private String productName;
    private String imageUrl;
    private String offerText;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private String redirectUrl;
    private Boolean isPromotional;  // For promotional cards
    private Integer viewCount;
}




















