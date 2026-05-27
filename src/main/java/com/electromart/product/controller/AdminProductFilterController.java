package com.electromart.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.ProductFilterRequest;
import com.electromart.product.service.AdminProductFilterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@CrossOrigin
public class AdminProductFilterController {

    private final AdminProductFilterService filterService;

    @PostMapping("/filter")
    public List<AdminProductFilterResponseDto> filter(
            @RequestBody ProductFilterRequest request
    ) {
        return filterService.filter(request);
    }
}

