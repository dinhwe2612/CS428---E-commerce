package com.catalog.catalog_service.controller;

import com.catalog.catalog_service.service.ProductSyncService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for synchronizing products with Elasticsearch.
 */
@RestController
@RequestMapping("/products/sync")
@Tag(name = "Product Sync", description = "Endpoints to sync products with Elasticsearch")
@RequiredArgsConstructor
@Slf4j
public class ProductSyncController {

    private final ProductSyncService productSyncService;

    @Operation(
            summary = "Sync products to Elasticsearch",
            description = "Triggers a full synchronization of all products into the Elasticsearch index",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products synced successfully",
                            content = @Content(mediaType = "text/plain"))
            }
    )
    @PostMapping("/elasticsearch")
    public ResponseEntity<String> syncToElasticsearch() {
        log.info("Received request to sync products to Elasticsearch");
        productSyncService.syncAllProducts();
        return ResponseEntity.ok("Products synced successfully to Elasticsearch");
    }

    @Operation(
            summary = "Delete Elasticsearch index",
            description = "Deletes the entire Elasticsearch index for products",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Elasticsearch index deleted successfully",
                            content = @Content(mediaType = "text/plain"))
            }
    )
    @DeleteMapping("/elasticsearch")
    public ResponseEntity<String> deleteElasticsearchIndex() {
        log.info("Received request to delete Elasticsearch index");
        productSyncService.deleteIndex();
        return ResponseEntity.ok("Elasticsearch index deleted successfully");
    }
}
