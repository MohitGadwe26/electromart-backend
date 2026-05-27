package com.electromart.product.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.electromart.product.Product;
import com.electromart.product.dto.ProductFilterRequest;
import com.electromart.product.dto.ProductListResponseDto;
import com.electromart.product.repository.ProductRepository;
import com.electromart.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductFilterService {

    private final ProductRepository productRepository;

   public List<ProductListResponseDto> filter(ProductFilterRequest req) {

    Specification<Product> spec =
            ProductSpecification.filter(
                    req.getCategoryId(),
                    req.getBrand(),
                    req.getMinPrice(),
                    req.getMaxPrice()
            );

    if (req.getAttributes() != null && !req.getAttributes().isEmpty()) {
        for (var entry : req.getAttributes().entrySet()) {
            spec = spec.and(
                    ProductSpecification.attributeFilter(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }
    }

    return productRepository.findAll(spec)
            .stream()
            .map(this::mapToListDto)
            .toList();
}

private ProductListResponseDto mapToListDto(Product product) {
    return ProductListResponseDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .brandName(product.getBrand().getName())
            .categoryId(product.getCategory().getId())
            .categoryName(product.getCategory().getName())
            .build();
}

}
