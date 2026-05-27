package com.electromart.product.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class HomepageCardDto {
    private Long id;
    private String cardText;
    private String categoryLink;
    private String cardType;
    private List<HomepageCardItemDto> items;
}