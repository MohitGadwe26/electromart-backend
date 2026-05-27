package com.electromart.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electromart.product.Attribute;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    List<Attribute> findByCategoryIdAndActiveTrue(Long categoryId);
    List<Attribute> findByCategoryIdAndFilterableTrueAndActiveTrue(
            Long categoryId
    );
}

