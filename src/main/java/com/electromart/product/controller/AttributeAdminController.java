package com.electromart.product.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.AttributeRequestDto;
import com.electromart.product.dto.AttributeResponseDto;
import com.electromart.product.service.AttributeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/attributes")
@RequiredArgsConstructor
@CrossOrigin
public class AttributeAdminController {

    private final AttributeService attributeService;

    @PostMapping
    public AttributeResponseDto create(@RequestBody AttributeRequestDto dto) {
        return attributeService.create(dto);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean active
    ) {
        attributeService.changeStatus(id, active);
    }

    @PatchMapping("/{id}/filterable")
    public void changeFilterable(
        @PathVariable Long id,
        @RequestParam Boolean filterable
) {
    attributeService.changeFilterable(id, filterable);
}

@PutMapping("/{id}")
public AttributeResponseDto update(
        @PathVariable Long id,
        @RequestBody AttributeRequestDto dto
) {
    return attributeService.update(id, dto);
}


}

