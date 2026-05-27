package com.electromart.product.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.electromart.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // ===== EXISTING METHODS =====
    List<Product> findByActiveTrue();
    List<Product> findByFeaturedTrueAndActiveTrue();
    long countByCategoryId(Long categoryId);
    long countByBrandId(Long brandId);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
    List<Product> findByBrand_NameIgnoreCase(String brand);

    // ===== HOMEPAGE CARD METHODS =====
    
    // 1. Recently Viewed Products - Using Pageable instead of LIMIT
    @Query(value = "SELECT p.* FROM products p " +
           "JOIN user_product_views uv ON uv.product_id = p.id " +
           "WHERE uv.user_id = :userId " +
           "ORDER BY uv.viewed_at DESC", nativeQuery = true)
    List<Product> findRecentlyViewedByUser(@Param("userId") Long userId, Pageable pageable);
    
    // 2. Featured Products (Fallback for new users)
    @Query("SELECT p FROM Product p WHERE p.featured = true AND p.active = true")
    List<Product> findTopNByFeaturedTrue(Pageable pageable);
    
    // 3. Active Products (Ultimate fallback)
    @Query("SELECT p FROM Product p WHERE p.active = true")
    List<Product> findTopNActiveProducts(Pageable pageable);
    
    // 4. Products from Browsed Categories - Using Pageable
    @Query(value = "SELECT DISTINCT p.* FROM products p " +
           "WHERE p.category_id IN (SELECT DISTINCT bh.category_id FROM user_browsing_history bh WHERE bh.user_id = :userId) " +
           "AND p.active = true", nativeQuery = true)
    List<Product> findProductsFromBrowsedCategories(@Param("userId") Long userId, Pageable pageable);
    
    // 5. Best Sellers (Fallback for Keep Shopping)
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.purchaseCount DESC")
    List<Product> findTopNBestSellers(Pageable pageable);
    
    // 6. Popular Products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.rating DESC")
    List<Product> findTopNPopularProducts(Pageable pageable);
    
    // 7. Products on Deal (Active discounts)
    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND p.discountPercentage IS NOT NULL " +
           "AND p.discountPercentage > 0")
    List<Product> findProductsOnDeal(Pageable pageable);
    
    // 8. Products with any discount (Fallback for deals)
    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND p.discountPercentage IS NOT NULL " +
           "AND p.discountPercentage > 0")
    List<Product> findTopNByDiscountPercentageNotNull(Pageable pageable);
    
    // 9. Dynamic products for homepage card (with filters)
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.active = true " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'price_asc' THEN p.price END ASC, " +
           "CASE WHEN :sortBy = 'price_desc' THEN p.price END DESC, " +
           "CASE WHEN :sortBy = 'newest' THEN p.createdAt END DESC, " +
           "CASE WHEN :sortBy = 'popularity' THEN p.purchaseCount END DESC")
    List<Product> findProductsForHomepageCard(
        @Param("categoryId") Long categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("sortBy") String sortBy,
        Pageable pageable
    );
}