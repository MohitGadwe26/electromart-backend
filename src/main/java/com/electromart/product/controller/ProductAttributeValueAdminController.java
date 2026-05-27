package com.electromart.product.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.ProductAttributeValueRequestDto;
import com.electromart.product.dto.ProductAttributeValueResponseDto;
import com.electromart.product.service.ProductAttributeValueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/product-attributes")
@RequiredArgsConstructor
@CrossOrigin
public class ProductAttributeValueAdminController {

    private final ProductAttributeValueService service;

    @PostMapping
    public ProductAttributeValueResponseDto create(
            @RequestBody ProductAttributeValueRequestDto dto
    ) {
        return service.create(dto);
    }
}

