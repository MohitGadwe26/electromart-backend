package com.electromart.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.electromart.product.Product;
import com.electromart.product.ProductImage;
import com.electromart.product.dto.ProductImageRequestDto;
import com.electromart.product.dto.ProductImageResponseDto;
import com.electromart.product.repository.ProductImageRepository;
import com.electromart.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductImageService {

    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;

       // 🌍 Public
    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getByProduct(Long productId) {
        log.info("Fetching images for product: {}", productId);
        List<ProductImage> images = imageRepository.findByProductIdAndActiveTrue(productId);
        log.info("Found {} images", images.size());
        
        return images.stream()
                .map(this::mapToDto)
                .toList();
    }

    

     // 🔐 Admin
    public ProductImageResponseDto add(ProductImageRequestDto dto) {
        log.info("Adding image to product: {}", dto.getProductId());
        
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // If primary image = true → unset existing primary
        if (Boolean.TRUE.equals(dto.getPrimaryImage())) {
            imageRepository.unsetPrimaryImages(product.getId());
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(dto.getImageUrl())
                .primaryImage(dto.getPrimaryImage() != null && dto.getPrimaryImage())
                .active(true)
                .displayOrder(getNextDisplayOrder(product.getId()))
                .build();

        ProductImage savedImage = imageRepository.save(image);
        log.info("Image saved with ID: {}", savedImage.getId());
        
        return mapToDto(savedImage);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting image: {}", id);
        ProductImage image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        image.setActive(false); // Soft delete instead of hard delete
        imageRepository.save(image);
    }

    @Transactional
    public void setPrimary(Long id) {
        log.info("Setting primary image: {}", id);
        
        ProductImage image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Reset others
        imageRepository.unsetPrimaryImages(image.getProduct().getId());

        image.setPrimaryImage(true);
        imageRepository.save(image);
        
        log.info("Primary image set successfully");
    }

       private Integer getNextDisplayOrder(Long productId) {
        return (int) imageRepository.countByProductId(productId);
    }


       private ProductImageResponseDto mapToDto(ProductImage image) {
        return ProductImageResponseDto.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .primaryImage(image.isPrimaryImage())
                .build();
    }

    // Add this method to ProductImageService.java
   // Add this method to ProductImageService.java
      public String getPrimaryImageUrl(Long productId) {
      return imageRepository.findByProductIdAndPrimaryImageTrueAndActiveTrue(productId)
        .map(ProductImage::getImageUrl)
        .orElse(null);
}
}

