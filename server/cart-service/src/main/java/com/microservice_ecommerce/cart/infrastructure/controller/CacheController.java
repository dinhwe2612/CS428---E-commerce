package com.microservice_ecommerce.cart.infrastructure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice_ecommerce.cart.domain.service.RecommendationService;
import com.microservice_ecommerce.cart.domain.service.impl.RecommendationServiceImpl;

import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/api/v1/cart/cache")
@Slf4j
public class CacheController {

    @Autowired
    private RecommendationServiceImpl recommendationServiceImpl;

    @PostMapping("/invalidate")
    public ResponseEntity<String> invalidateCache() {
        recommendationServiceImpl.invalidateAllCache();
        return ResponseEntity.ok("Cache invalidated successfully");
    }

    @PostMapping("/invalidate/products")
    public ResponseEntity<String> invalidateProductsCache() {
        recommendationServiceImpl.invalidateProductsCache();
        recommendationServiceImpl.invalidatePopularProductsCache();
        return ResponseEntity.ok("Products cache invalidated");
    }

    @GetMapping("/status")
    public ResponseEntity<String> getCacheStatus() {
        return ResponseEntity.ok("Cache size: " + recommendationServiceImpl.getCacheSize() + " entries");
    }
}
