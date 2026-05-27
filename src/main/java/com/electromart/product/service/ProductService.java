package com.electromart.product.service;


import com.electromart.product.Brand;
import com.electromart.product.Category;
import com.electromart.product.Product;
import com.electromart.product.ProductImage;
import com.electromart.product.dto.ProductCreateRequest;
import com.electromart.product.dto.ProductResponseDto;
import com.electromart.product.dto.ProductUpdateRequest;
import com.electromart.product.repository.BrandRepository;
import com.electromart.product.repository.CategoryRepository;
import com.electromart.product.repository.ProductImageRepository;
import com.electromart.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductImageRepository productImageRepository;
    

    // CREATE
    public ProductResponseDto create(ProductCreateRequest request) {

       Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .sku(request.getSku())
                .category(category)
                .brand(brand)
                .active(true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());

        return mapToDto(productRepository.save(product));
    }

    // UPDATE (PATCH)
    public ProductResponseDto update(Long id, ProductUpdateRequest request) {
        log.info("Updating product: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getName() != null)
            product.setName(request.getName());

        if (request.getDescription() != null)
            product.setDescription(request.getDescription());

        if (request.getPrice() != null)
            product.setPrice(request.getPrice());

        if (request.getStock() != null)
            product.setStock(request.getStock());

        if (request.getFeatured() != null)
            product.setFeatured(request.getFeatured());

        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }
    
    // CHANGE STATUS
    public void changeStatus(Long id, Boolean active) {
        log.info("Changing product status: {} to {}", id, active);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(active);
        productRepository.save(product);
    }
    
    // UPDATE STOCK
    public void updateStock(Long id, Integer stock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStock(stock);
        productRepository.save(product);
    }

    // SOFT DELETE
    public void delete(Long id) {
        log.info("Soft deleting product: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    // PUBLIC
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAll() {
        log.info("Fetching all active products");
        List<Product> products = productRepository.findByActiveTrue();
        log.info("Found {} products", products.size());
        
        return products.stream()
            .map(this::mapToDto)
            .toList();
    }

    // Get Featured Products
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getFeaturedProducts() {
        log.info("Fetching featured products");
        List<Product> products = productRepository.findByFeaturedTrueAndActiveTrue();
        log.info("Found {} featured products", products.size());
        
        return products.stream()
            .map(this::mapToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getByCategory(Long categoryId) {
        log.info("Fetching products for category: {}", categoryId);
        List<Product> products = productRepository.findByCategoryId(categoryId);
        log.info("Found {} products", products.size());
        
        return products.stream()
            .map(this::mapToDto)
            .toList();
    }


   @Transactional(readOnly = true)
    public ProductResponseDto getById(Long id) {
        log.info("Fetching product by ID: {}", id);
        Product product = productRepository.findById(id)
         .orElseThrow(() -> new RuntimeException("Product not found"));
        
        return mapToDto(product);
    }

    // MAPPER
    private ProductResponseDto mapToDto(Product product) {
        log.debug("Mapping product to DTO: {}", product.getId());
        
        // Get images from ProductImage table
        List<ProductImage> images = productImageRepository.findByProductIdAndActiveTrue(product.getId());
        
        String imageUrl = null;
        if (images != null && !images.isEmpty()) {
            log.debug("Found {} images for product: {}", images.size(), product.getName());
            
            // Try to get primary image first
            imageUrl = images.stream()
                .filter(ProductImage::isPrimaryImage)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
            
            // If no primary image, get the first one
            if (imageUrl == null) {
                imageUrl = images.get(0).getImageUrl();
                log.debug("Using first image as fallback: {}", imageUrl);
            } else {
                log.debug("Using primary image: {}", imageUrl);
            }
        } else {
            log.warn("No images found for product: {}", product.getName());
        }
        
        return ProductResponseDto.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .stock(product.getStock())
            .sku(product.getSku())
            .active(product.isActive())
            .featured(product.isFeatured())
            .imageUrl(imageUrl)
            .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
            .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
            .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
            .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
            .build();
    }
}
