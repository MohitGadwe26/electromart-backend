package com.electromart.product.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.electromart.product.dto.ProductCreateRequest;
import com.electromart.product.dto.ProductResponseDto;
import com.electromart.product.dto.ProductUpdateRequest;
import com.electromart.product.service.ProductService;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@CrossOrigin
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponseDto create(@RequestBody ProductCreateRequest request) {
        return productService.create(request);
    }

    @PatchMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequest request) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean active
    ) {
        productService.changeStatus(id, active);
    }

    @PatchMapping("/{id}/stock")
    public void updateStock(
            @PathVariable Long id,
            @RequestParam Integer stock
    ) {
        productService.updateStock(id, stock);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}

