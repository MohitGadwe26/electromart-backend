package com.electromart.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electromart.product.ProductAttributeValue;

public interface ProductAttributeValueRepository
        extends JpaRepository<ProductAttributeValue, Long> {

    List<ProductAttributeValue> findByProductId(Long productId);
}

