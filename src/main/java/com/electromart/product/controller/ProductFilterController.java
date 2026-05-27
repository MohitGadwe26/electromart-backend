package com.electromart.product.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.electromart.product.dto.ProductFilterRequest;
import com.electromart.product.dto.ProductListResponseDto;
import com.electromart.product.service.ProductFilterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductFilterController {

    private final ProductFilterService filterService;

    // Existing POST endpoint
    @PostMapping("/filter")
    public List<ProductListResponseDto> filter(@RequestBody ProductFilterRequest request) {
        return filterService.filter(request);
    }

    // NEW GET endpoint for banner navigation
    @GetMapping("/filter")
    public List<ProductListResponseDto> filterGet(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String sleeveType,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String sortBy) {
        
        ProductFilterRequest request = ProductFilterRequest.builder()
                .categoryId(categoryId)
                .brand(brand)
                .minPrice(minPrice != null ? BigDecimal.valueOf(minPrice) : null)
                .maxPrice(maxPrice != null ? BigDecimal.valueOf(maxPrice) : null)
                .gender(gender)
                .productType(productType)
                .sleeveType(sleeveType)
                .pattern(pattern)
                .featured(featured)
                .build();
        
        List<ProductListResponseDto> products = filterService.filter(request);
        
        // Apply sorting if needed (you can also handle sorting in your existing service)
        if (sortBy != null && products != null) {
            switch (sortBy) {
                case "price_asc":
                    products.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
                    break;
                case "price_desc":
                    products.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
                    break;
                case "best_sellers":
                    // Add best sellers sorting logic if you have sales data
                    break;
            }
        }
        
        return products;
    }
}