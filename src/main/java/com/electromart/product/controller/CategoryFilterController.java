package com.electromart.product.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.CategoryFilterResponse;
import com.electromart.product.service.CategoryFilterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin
public class CategoryFilterController {

    private final CategoryFilterService categoryFilterService;

    @GetMapping("/{categoryId}/filters")
    public CategoryFilterResponse getCategoryFilters(
            @PathVariable Long categoryId
    ) {
        return categoryFilterService.getFilters(categoryId);
    }
}

