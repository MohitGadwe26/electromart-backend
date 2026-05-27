package com.electromart.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.electromart.product.dto.AttributeFilterDto;
import com.electromart.product.dto.CategoryFilterResponse;
import com.electromart.product.repository.AttributeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryFilterService {

    private final AttributeRepository attributeRepository;

    public CategoryFilterResponse getFilters(Long categoryId) {

        List<AttributeFilterDto> attributeFilters =
                attributeRepository
                        .findByCategoryIdAndFilterableTrueAndActiveTrue(categoryId)
                        .stream()
                        .map(attr -> AttributeFilterDto.builder()
                                .id(attr.getId())
                                .name(attr.getName())
                                .dataType(attr.getDataType())
                                .unit(attr.getUnit())
                                .build()
                        )
                        .toList();

        return CategoryFilterResponse.builder()
                .price(true)   // common filter
                .brand(true)   // common filter
                .attributes(attributeFilters)
                .build();
    }
}

