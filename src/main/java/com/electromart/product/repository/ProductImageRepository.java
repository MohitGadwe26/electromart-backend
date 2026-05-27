package com.electromart.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.electromart.product.ProductImage;

public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);
    List<ProductImage> findByProductIdAndActiveTrue(Long productId);
    List<ProductImage> findByProductIdAndPrimaryImageTrue(Long productId);
    // ✅ Add this method - Custom query to unset primary images
    @Modifying
    @Transactional
    @Query("UPDATE ProductImage i SET i.primaryImage = false WHERE i.product.id = :productId")
    void unsetPrimaryImages(@Param("productId") Long productId);
    
    // ✅ Add this method - Count images for a product
    long countByProductId(Long productId);
    long countByProductIdAndActiveTrue(Long productId);

    // ✅ Optional: Delete all images for a product
    @Modifying
    @Transactional
    void deleteByProductId(Long productId);

    @Modifying
    @Transactional
    @Query("UPDATE ProductImage i SET i.active = false WHERE i.product.id = :productId")
    void softDeleteByProductId(@Param("productId") Long productId);


    // ✅ Optional: Find primary image for a product
    Optional<ProductImage> findByProductIdAndPrimaryImageTrueAndActiveTrue(Long productId);   
}

