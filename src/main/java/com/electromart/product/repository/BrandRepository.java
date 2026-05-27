package com.electromart.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electromart.product.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}

