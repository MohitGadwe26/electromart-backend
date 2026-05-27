package com.electromart.product.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.ProductImageRequestDto;
import com.electromart.product.dto.ProductImageResponseDto;
import com.electromart.product.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/product-images")
@RequiredArgsConstructor
@CrossOrigin
public class ProductImageAdminController {

    private final ProductImageService imageService;

    @PostMapping
    public ProductImageResponseDto add(
            @RequestBody ProductImageRequestDto dto
    ) {
        return imageService.add(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        imageService.delete(id);
    }

    @PatchMapping("/{id}/primary")
    public void setPrimary(@PathVariable Long id) {
        imageService.setPrimary(id);
    }
}

