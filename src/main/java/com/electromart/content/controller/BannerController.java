package com.electromart.content.controller;

import com.electromart.content.dto.BannerRequestDto;
import com.electromart.content.dto.BannerResponseDto;
import com.electromart.content.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/active")
    public ResponseEntity<List<BannerResponseDto>> getActiveBanners() {
        return ResponseEntity.ok(bannerService.getActiveBanners());
    }

    // @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<List<BannerResponseDto>> getAllBanners() {
    //     return ResponseEntity.ok(bannerService.getAllBanners());
    // }

    // @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<BannerResponseDto> getBannerById(@PathVariable Long id) {
    //     return ResponseEntity.ok(bannerService.getBannerById(id));
    // }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BannerResponseDto> createBanner(@RequestBody BannerRequestDto dto) {
        return ResponseEntity.ok(bannerService.createBanner(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BannerResponseDto> updateBanner(@PathVariable Long id, @RequestBody BannerRequestDto dto) {
        return ResponseEntity.ok(bannerService.updateBanner(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id, @RequestParam boolean active) {
        bannerService.toggleActive(id, active);
        return ResponseEntity.ok().build();
    }
}