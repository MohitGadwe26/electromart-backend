package com.electromart.product.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.CategoryRequestDTO;
import com.electromart.product.dto.CategoryResponseDto;
import com.electromart.product.dto.CategoryUpdateRequestDTO;
import com.electromart.product.service.CategoryService;

import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@CrossOrigin
public class AdminCategoryController {

    private final CategoryService categoryService;

   
    @PostMapping
    public CategoryResponseDto create(@RequestBody CategoryRequestDTO dto) {
        return categoryService.create(dto);
    }

    @PatchMapping("/{id}")
    public CategoryResponseDto updateCategory(
        @PathVariable Long id,
        @RequestBody CategoryUpdateRequestDTO dto
) {
    return categoryService.updateCategory(id, dto);
}


    @DeleteMapping("/{id}")
      public void deleteCategory(@PathVariable Long id) {
      categoryService.deleteCategory(id);
    }
}


