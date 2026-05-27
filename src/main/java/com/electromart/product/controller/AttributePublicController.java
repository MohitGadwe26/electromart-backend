package com.electromart.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.AttributeResponseDto;
import com.electromart.product.service.AttributeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
@CrossOrigin
public class AttributePublicController {

    private final AttributeService attributeService;

    @GetMapping("/category/{categoryId}")
    public List<AttributeResponseDto> getByCategory(@PathVariable Long categoryId) {
        return attributeService.getByCategory(categoryId);
    }
}

