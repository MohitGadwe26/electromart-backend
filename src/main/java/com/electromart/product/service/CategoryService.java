package com.electromart.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.electromart.product.Category;
import com.electromart.product.dto.CategoryRequestDTO;
import com.electromart.product.dto.CategoryResponseDto;
import com.electromart.product.dto.CategoryUpdateRequestDTO;
import com.electromart.product.repository.CategoryRepository;
import com.electromart.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryResponseDto create(CategoryRequestDTO dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new RuntimeException("Category already exists");
        }

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .icon(dto.getIcon())
                .active(true)
                .build();

        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.setParent(parent);
        }

        return mapToDto(categoryRepository.save(category));
    }

    public CategoryResponseDto updateCategory(Long id, CategoryUpdateRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (dto.getName() != null && !dto.getName().equalsIgnoreCase(category.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new RuntimeException("Category name already exists");
            }
            category.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }

        if (dto.getImageUrl() != null) {
            category.setImageUrl(dto.getImageUrl());
        }

        if (dto.getIcon() != null) {
            category.setIcon(dto.getIcon());
        }

        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }

        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));

            if (parent.getId().equals(category.getId())) {
                throw new RuntimeException("Category cannot be parent of itself");
            }
            category.setParent(parent);
        }

        return mapToDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        long productCount = productRepository.countByCategoryId(id);

        if (productCount > 0) {
            throw new RuntimeException(
                "Cannot delete category. " + productCount + " products are linked."
            );
        }

        categoryRepository.delete(category);
    }

    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findByActiveTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
    
    public CategoryResponseDto getCategoryResponseById(Long id) {
        Category category = getById(id);
        return mapToDto(category);
    }

    private CategoryResponseDto mapToDto(Category category) {
        long productCount = productRepository.countByCategoryId(category.getId());
        
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .cardType(category.getCardType())
                .icon(category.getIcon())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .active(category.isActive())
                .productCount(productCount)
                .build();
    }
}