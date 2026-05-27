package com.electromart.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.ProductImageResponseDto;
import com.electromart.product.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductImagePublicController {

    private final ProductImageService imageService;

    @GetMapping("/{productId}/images")
    public List<ProductImageResponseDto> getImages(
            @PathVariable Long productId
    ) {
        return imageService.getByProduct(productId);
    }
}

