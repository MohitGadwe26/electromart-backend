package com.electromart.product.service;

import org.springframework.stereotype.Service;


import com.electromart.product.Attribute;
import com.electromart.product.Product;
import com.electromart.product.ProductAttributeValue;
import com.electromart.product.dto.ProductAttributeValueRequestDto;
import com.electromart.product.dto.ProductAttributeValueResponseDto;
import com.electromart.product.repository.AttributeRepository;
import com.electromart.product.repository.ProductAttributeValueRepository;
import com.electromart.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductAttributeValueService {

    private final ProductAttributeValueRepository repository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;

    public ProductAttributeValueResponseDto create(ProductAttributeValueRequestDto dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Attribute attribute = attributeRepository.findById(dto.getAttributeId())
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        ProductAttributeValue pav = ProductAttributeValue.builder()
                .product(product)
                .attribute(attribute)
                .value(dto.getValue())
                .build();

        return mapToDto(repository.save(pav));
    }

    private ProductAttributeValueResponseDto mapToDto(ProductAttributeValue pav) {
        return ProductAttributeValueResponseDto.builder()
                .id(pav.getId())
                .productId(pav.getProduct().getId())
                .attributeId(pav.getAttribute().getId())
                .attributeName(pav.getAttribute().getName())
                .value(pav.getValue())
                .build();
    }
}

