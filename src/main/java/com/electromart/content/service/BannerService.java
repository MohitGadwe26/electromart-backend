package com.electromart.content.service;

import com.electromart.content.dto.BannerRequestDto;
import com.electromart.content.dto.BannerResponseDto;
import com.electromart.content.entity.Banner;
import com.electromart.content.repository.BannerRepository;
import com.electromart.product.dto.ProductFilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannerService {

    private final BannerRepository bannerRepository;

    @Transactional(readOnly = true)
    public List<BannerResponseDto> getActiveBanners() {
        return bannerRepository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::toDtoWithFilter)
                .collect(Collectors.toList());
    }

    @Transactional
    public BannerResponseDto createBanner(BannerRequestDto dto) {
        Banner banner = Banner.builder()
                .imageUrl(dto.getImageUrl())
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .description(dto.getDescription())
                .discountText(dto.getDiscountText())
                .badgeText(dto.getBadgeText())
                .categoryId(dto.getCategoryId())
                .brand(dto.getBrand())
                .minPrice(dto.getMinPrice())
                .maxPrice(dto.getMaxPrice())
                .sortBy(dto.getSortBy())
                .gender(dto.getGender())
                .productType(dto.getProductType())
                .sleeveType(dto.getSleeveType())
                .pattern(dto.getPattern())
                .featured(dto.getFeatured())
                .displayOrder(dto.getDisplayOrder())
                .active(true)
                .build();

        Banner saved = bannerRepository.save(banner);
        log.info("Created banner: {}", saved.getTitle());
        return toDtoWithFilter(saved);
    }

    @Transactional
    public BannerResponseDto updateBanner(Long id, BannerRequestDto dto) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));

        if (dto.getImageUrl() != null) banner.setImageUrl(dto.getImageUrl());
        if (dto.getTitle() != null) banner.setTitle(dto.getTitle());
        if (dto.getSubtitle() != null) banner.setSubtitle(dto.getSubtitle());
        if (dto.getDescription() != null) banner.setDescription(dto.getDescription());
        if (dto.getDiscountText() != null) banner.setDiscountText(dto.getDiscountText());
        if (dto.getBadgeText() != null) banner.setBadgeText(dto.getBadgeText());
        if (dto.getCategoryId() != null) banner.setCategoryId(dto.getCategoryId());
        if (dto.getBrand() != null) banner.setBrand(dto.getBrand());
        if (dto.getMinPrice() != null) banner.setMinPrice(dto.getMinPrice());
        if (dto.getMaxPrice() != null) banner.setMaxPrice(dto.getMaxPrice());
        if (dto.getSortBy() != null) banner.setSortBy(dto.getSortBy());
        if (dto.getGender() != null) banner.setGender(dto.getGender());
        if (dto.getProductType() != null) banner.setProductType(dto.getProductType());
        if (dto.getSleeveType() != null) banner.setSleeveType(dto.getSleeveType());
        if (dto.getPattern() != null) banner.setPattern(dto.getPattern());
        if (dto.getFeatured() != null) banner.setFeatured(dto.getFeatured());
        if (dto.getDisplayOrder() != null) banner.setDisplayOrder(dto.getDisplayOrder());

        Banner updated = bannerRepository.save(banner);
        log.info("Updated banner: {}", id);
        return toDtoWithFilter(updated);
    }

    @Transactional
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
        log.info("Deleted banner: {}", id);
    }

    @Transactional
    public void toggleActive(Long id, boolean active) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));
        banner.setActive(active);
        bannerRepository.save(banner);
    }

    private BannerResponseDto toDtoWithFilter(Banner banner) {
        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .categoryId(banner.getCategoryId())
                .brand(banner.getBrand())
                .minPrice(banner.getMinPrice())
                .maxPrice(banner.getMaxPrice())
                .gender(banner.getGender())
                .productType(banner.getProductType())
                .sleeveType(banner.getSleeveType())
                .pattern(banner.getPattern())
                .featured(banner.getFeatured())
                .build();

        String redirectUrl = buildRedirectUrlFromFilter(filterRequest, banner.getSortBy());

        return BannerResponseDto.builder()
                .id(banner.getId())
                .imageUrl(banner.getImageUrl())
                .title(banner.getTitle())
                .subtitle(banner.getSubtitle())
                .description(banner.getDescription())
                .discountText(banner.getDiscountText())
                .badgeText(banner.getBadgeText())
                .filterRequest(filterRequest)
                .redirectUrl(redirectUrl)
                .displayOrder(banner.getDisplayOrder())
                .active(banner.isActive())
                .build();
    }

    private String buildRedirectUrlFromFilter(ProductFilterRequest filter, String sortBy) {
        StringBuilder url = new StringBuilder("/products/filter?");
        List<String> params = new ArrayList<>();

        if (filter.getCategoryId() != null) {
            params.add("categoryId=" + filter.getCategoryId());
        }
        if (filter.getBrand() != null) {
            params.add("brand=" + URLEncoder.encode(filter.getBrand(), StandardCharsets.UTF_8));
        }
        if (filter.getMinPrice() != null) {
            params.add("minPrice=" + filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null) {
            params.add("maxPrice=" + filter.getMaxPrice());
        }
        if (filter.getGender() != null) {
            params.add("gender=" + filter.getGender());
        }
        if (filter.getProductType() != null) {
            params.add("productType=" + URLEncoder.encode(filter.getProductType(), StandardCharsets.UTF_8));
        }
        if (filter.getSleeveType() != null) {
            params.add("sleeveType=" + URLEncoder.encode(filter.getSleeveType(), StandardCharsets.UTF_8));
        }
        if (filter.getPattern() != null) {
            params.add("pattern=" + URLEncoder.encode(filter.getPattern(), StandardCharsets.UTF_8));
        }
        if (filter.getFeatured() != null) {
            params.add("featured=" + filter.getFeatured());
        }
        if (sortBy != null) {
            params.add("sortBy=" + sortBy);
        }

        url.append(String.join("&", params));
        return url.toString();
    }
}