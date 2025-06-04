package com.catalog.catalog_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.catalog_service.service.ProductSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("products/sync")
@RequiredArgsConstructor
@Slf4j
public class ProductSyncController {

    private final ProductSyncService productSyncService;

    @PostMapping("/elasticsearch")
    public ResponseEntity<String> syncToElasticsearch() {
        log.info("Received request to sync products to Elasticsearch");
        productSyncService.syncAllProducts();
        return ResponseEntity.ok("Products synced successfully to Elasticsearch");
    }

    @DeleteMapping("/elasticsearch")
    public ResponseEntity<String> deleteElasticsearchIndex() {
        log.info("Received request to delete Elasticsearch index");
        productSyncService.deleteIndex();
        return ResponseEntity.ok("Elasticsearch index deleted successfully");
    }
} 