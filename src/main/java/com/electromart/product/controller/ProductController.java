package com.electromart.product.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.ProductResponseDto;
import com.electromart.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponseDto> getAll() {
        return productService.getAll();
    }

    @GetMapping("/featured")
    public List<ProductResponseDto> getFeatured() {
        return productService.getFeaturedProducts();
    }

    @GetMapping("/{id}")
    public ProductResponseDto getById(@PathVariable Long id) {
        return productService.getById(id);
    }
}
