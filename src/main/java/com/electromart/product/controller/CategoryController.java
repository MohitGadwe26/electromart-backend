package com.electromart.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.CategoryResponseDto;
import com.electromart.product.dto.ProductResponseDto;
import com.electromart.product.service.CategoryService;
import com.electromart.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public List<CategoryResponseDto> getAll() {
        return categoryService.getAll();
    }
    
    @GetMapping("/{categoryId}")
    public CategoryResponseDto getCategoryById(@PathVariable Long categoryId) {
        return categoryService.getCategoryResponseById(categoryId);
    }

    @GetMapping("/{categoryId}/products")
    public List<ProductResponseDto> getByCategory(@PathVariable Long categoryId) {
        return productService.getByCategory(categoryId);
    }
}