package com.electromart.product.service;

import com.electromart.product.Category;
import com.electromart.product.Product;
import com.electromart.product.dto.HomepageCardDto;
import com.electromart.product.dto.HomepageCardItemDto;
import com.electromart.product.repository.CategoryRepository;
import com.electromart.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomepageService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;

    @Value("${homepage.default.product.limit:4}")
    private int defaultProductLimit;

    public List<HomepageCardDto> getHomepageCards(Long userId) {
        List<HomepageCardDto> cards = new ArrayList<>();
        
        List<Category> categories = categoryRepository
            .findByShowOnHomepageTrueAndActiveTrueOrderByHomepageDisplayOrderAsc();
        
        log.info("Building {} homepage cards for user: {}", categories.size(), userId);
        
        for (Category category : categories) {
            HomepageCardDto card = buildCardByType(category, userId);
            if (card != null && !card.getItems().isEmpty()) {
                cards.add(card);
            }
        }
        
        return cards;
    }

    private HomepageCardDto buildCardByType(Category category, Long userId) {
        String cardType = category.getCardType() != null ? category.getCardType() : "PRODUCT_CARD";
        
        switch (cardType) {
            case "RECENTLY_VIEWED":
                return buildRecentlyViewedCard(category, userId);
            case "KEEP_SHOPPING":
                return buildKeepShoppingCard(category, userId);
            case "DEALS":
                return buildDealsCard(category);
            case "PROMOTIONAL":
                return buildPromotionalCard(category);
            default:
                return buildProductCard(category);
        }
    }

    private HomepageCardDto buildRecentlyViewedCard(Category category, Long userId) {
        int productLimit = getProductLimit(category);
        Pageable pageable = PageRequest.of(0, productLimit);
        
        List<Product> products = new ArrayList<>();
        
        if (userId != null) {
            products = productRepository.findRecentlyViewedByUser(userId, pageable);
        }
        
        if (products.isEmpty()) {
            log.info("No recently viewed products for user {}, showing featured products", userId);
            products = productRepository.findTopNByFeaturedTrue(pageable);
        }
        
        if (products.isEmpty()) {
            products = productRepository.findTopNActiveProducts(pageable);
        }
        
        String cardText = category.getHomepageCardTitle();
        if (cardText == null || cardText.isEmpty()) {
            cardText = "Pick up where you left off";
        }
        
        return HomepageCardDto.builder()
            .id(category.getId())
            .cardText(cardText)
            .cardType("RECENTLY_VIEWED")
            .categoryLink("/products/recently-viewed")
            .items(products.stream()
                .map(this::toCardItem)
                .collect(Collectors.toList()))
            .build();
    }

    private HomepageCardDto buildKeepShoppingCard(Category category, Long userId) {
        int productLimit = getProductLimit(category);
        Pageable pageable = PageRequest.of(0, productLimit);
        
        List<Product> products = new ArrayList<>();
        
        if (userId != null) {
            products = productRepository.findProductsFromBrowsedCategories(userId, pageable);
        }
        
        if (products.isEmpty()) {
            log.info("No browsing history for user {}, showing best sellers", userId);
            products = productRepository.findTopNBestSellers(pageable);
        }
        
        if (products.isEmpty()) {
            products = productRepository.findTopNPopularProducts(pageable);
        }
        
        String cardText = category.getHomepageCardTitle();
        if (cardText == null || cardText.isEmpty()) {
            cardText = "Keep shopping for";
        }
        
        return HomepageCardDto.builder()
            .id(category.getId())
            .cardText(cardText)
            .cardType("KEEP_SHOPPING")
            .categoryLink("/products/recommended")
            .items(products.stream()
                .map(this::toCardItem)
                .collect(Collectors.toList()))
            .build();
    }

    private HomepageCardDto buildDealsCard(Category category) {
        int productLimit = getProductLimit(category);
        Pageable pageable = PageRequest.of(0, productLimit);
        
        List<Product> products = productRepository.findProductsOnDeal(pageable);
        
        if (products.isEmpty()) {
            products = productRepository.findTopNByDiscountPercentageNotNull(pageable);
        }
        
        String cardText = category.getHomepageCardTitle();
        if (cardText == null || cardText.isEmpty()) {
            cardText = "Continue shopping deals";
        }
        
        return HomepageCardDto.builder()
            .id(category.getId())
            .cardText(cardText)
            .cardType("DEALS")
            .categoryLink("/products/deals")
            .items(products.stream()
                .map(this::toCardItem)
                .collect(Collectors.toList()))
            .build();
    }

    private HomepageCardDto buildPromotionalCard(Category category) {
        String cardText = category.getHomepageCardTitle();
        if (cardText == null || cardText.isEmpty()) {
            cardText = "Get bulk discounts + 10% Welcome cashback!";
        }
        
        HomepageCardItemDto promoItem = HomepageCardItemDto.builder()
            .id(category.getId())
            .productName(cardText)
            .imageUrl(category.getImageUrl())
            .offerText("Register now")
            .redirectUrl(category.getHomepageRedirectUrl() != null ? 
                category.getHomepageRedirectUrl() : "/register")
            .isPromotional(true)
            .build();
        
        return HomepageCardDto.builder()
            .id(category.getId())
            .cardText(cardText)
            .cardType("PROMOTIONAL")
            .categoryLink("/register")
            .items(List.of(promoItem))
            .build();
    }

    private HomepageCardDto buildProductCard(Category category) {
        int productLimit = getProductLimit(category);
        Pageable pageable = PageRequest.of(0, productLimit);
        
        BigDecimal minPrice = category.getHomepageMinPrice();
        BigDecimal maxPrice = category.getHomepageMaxPrice();
        String sortBy = getSortByForCategory(category);
        
        List<Product> products = productRepository.findProductsForHomepageCard(
            category.getId(),
            minPrice,
            maxPrice,
            sortBy,
            pageable
        );
        
        String cardText = category.getHomepageCardTitle();
        if (cardText == null || cardText.isEmpty()) {
            cardText = generateDefaultCardText(category);
        }
        
        return HomepageCardDto.builder()
            .id(category.getId())
            .cardText(cardText)
            .cardType("PRODUCT_CARD")
            .categoryLink("/products?category=" + category.getId())
            .items(products.stream()
                .map(this::toCardItem)
                .collect(Collectors.toList()))
            .build();
    }

    private HomepageCardItemDto toCardItem(Product product) {
        String imageUrl = productImageService.getPrimaryImageUrl(product.getId());
        String offerText = calculateOfferText(product);
        
        Integer viewCount = product.getViewCount() != null ? product.getViewCount() : 0;
        
        return HomepageCardItemDto.builder()
            .id(product.getId())
            .productName(product.getName())
            .imageUrl(imageUrl)
            .offerText(offerText)
            .price(product.getPrice())
            .originalPrice(product.getOriginalPrice())
            .discountPercentage(product.getDiscountPercentage())
            .redirectUrl("/product/" + product.getId())
            .isPromotional(false)
            .viewCount(viewCount)
            .build();
    }
    
    private int getProductLimit(Category category) {
        return category.getHomepageProductLimit() != null ? 
            category.getHomepageProductLimit() : defaultProductLimit;
    }

    private String generateDefaultCardText(Category category) {
        if (category.getHomepageMaxPrice() != null) {
            return "Under ₹" + category.getHomepageMaxPrice().intValue() + " | " + category.getName();
        }
        if (category.getHomepageMinPrice() != null) {
            return "Starting ₹" + category.getHomepageMinPrice().intValue() + " | " + category.getName();
        }
        return "Up to 50% off | " + category.getName();
    }

    private String getSortByForCategory(Category category) {
        if (category.getId() == 19) {
            return "price_asc";
        }
        return "popularity";
    }

    private String calculateOfferText(Product product) {
        if (product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0) {
            return product.getDiscountPercentage() + "% off";
        }
        if (product.getOfferText() != null) {
            return product.getOfferText();
        }
        return "₹" + product.getPrice();
    }
}