package com.electromart.product.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.BrandRequestDto;
import com.electromart.product.dto.BrandResponseDto;
import com.electromart.product.service.BrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
@CrossOrigin
public class BrandAdminController {

    private final BrandService brandService;

    @PostMapping
    public BrandResponseDto create(@RequestBody BrandRequestDto dto) {
        return brandService.create(dto);
    }

    @PutMapping("/{id}")
    public BrandResponseDto update(
            @PathVariable Long id,
            @RequestBody BrandRequestDto dto
    ) {
        return brandService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean active
    ) {
        brandService.changeStatus(id, active);
    }

    @DeleteMapping("/{id}")
    public void deleteBrand(@PathVariable Long id) {
         brandService.deleteBrand(id);
    }
}
  
