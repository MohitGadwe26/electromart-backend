package com.electromart.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.BrandResponseDto;
import com.electromart.product.service.BrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@CrossOrigin
public class BrandPublicController {

    private final BrandService brandService;

    @GetMapping
    public List<BrandResponseDto> getAllBrands() {
        return brandService.getAllActive();
    }
}

