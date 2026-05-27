package com.electromart.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electromart.product.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
     List<Category> findByActiveTrue();
     boolean existsByNameIgnoreCase(String name);
     // Add this method
    List<Category> findByShowOnHomepageTrueAndActiveTrueOrderByHomepageDisplayOrderAsc();
}

