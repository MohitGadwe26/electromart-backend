package com.electromart.product.service;

import java.util.List;

import org.springframework.stereotype.Service;


import com.electromart.product.Brand;
import com.electromart.product.dto.BrandRequestDto;
import com.electromart.product.dto.BrandResponseDto;
import com.electromart.product.repository.BrandRepository;
import com.electromart.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    // 🌍 Public
    public List<BrandResponseDto> getAllActive() {
        return brandRepository.findByActiveTrue()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // 🔐 Admin
    public BrandResponseDto create(BrandRequestDto dto) {

        if (brandRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new RuntimeException("Brand already exists");
        }

        Brand brand = Brand.builder()
                .name(dto.getName())
                .logoUrl(dto.getLogoUrl())
                .active(true)
                .build();

        return mapToDto(brandRepository.save(brand));
    }

    public BrandResponseDto update(Long id, BrandRequestDto dto) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brand.setName(dto.getName());
        brand.setLogoUrl(dto.getLogoUrl());

        return mapToDto(brandRepository.save(brand));
    }

    public void changeStatus(Long id, Boolean active) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        brand.setActive(active);
        brandRepository.save(brand);
    }

    public void deleteBrand(Long id) {

    Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Brand not found"));

    long productCount = productRepository.countByBrandId(id);

    if (productCount > 0) {
        throw new RuntimeException(
            "Cannot delete brand. " + productCount + " products are linked."
        );
    }

    brandRepository.delete(brand);
}


    // 🔁 Mapper
    private BrandResponseDto mapToDto(Brand brand) {
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logoUrl(brand.getLogoUrl())
                .active(brand.isActive())
                .build();
    }
}

