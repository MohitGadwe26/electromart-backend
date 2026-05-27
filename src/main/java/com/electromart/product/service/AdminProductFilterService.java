package com.electromart.product.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import com.electromart.product.Product;
import com.electromart.product.controller.AdminProductFilterResponseDto;
import com.electromart.product.dto.ProductFilterRequest;
import com.electromart.product.repository.ProductRepository;
import com.electromart.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductFilterService {

    private final ProductRepository productRepository;

    public List<AdminProductFilterResponseDto> filter(
            ProductFilterRequest req
    ) {

        Specification<Product> spec =
                ProductSpecification.filter(
                        req.getCategoryId(),
                        req.getBrand(),
                        req.getMinPrice(),
                        req.getMaxPrice()
                );

        if (req.getAttributes() != null && !req.getAttributes().isEmpty()) {
            for (var entry : req.getAttributes().entrySet()) {
                spec = spec.and(
                        ProductSpecification.attributeFilter(
                                entry.getKey(),
                                entry.getValue()
                        )
                );
            }
        }

        return productRepository.findAll(spec)
                .stream()
                .map(this::mapToAdminDto)
                .toList();
    }

    private AdminProductFilterResponseDto mapToAdminDto(Product product) {

        Map<String, String> attrMap = new HashMap<>();
        product.getAttributeValues().forEach(av ->
            attrMap.put(
                av.getAttribute().getName(),
                av.getValue()
            )
        );

        return AdminProductFilterResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .sku(product.getSku())
                .active(product.isActive())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())
                .attributes(attrMap)
                .build();
    }
}

