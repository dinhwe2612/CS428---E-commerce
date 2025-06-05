package com.catalog.catalog_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.catalog_service.dto.AutocompleteResponse;
import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.request.CreateProductRequest;
import com.catalog.catalog_service.dto.request.UpdateProductRequest;
import com.catalog.catalog_service.service.ProductService;
import com.catalog.catalog_service.service.ProductSyncService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductSyncService productSyncService;

    @Autowired
    public ProductController(ProductService productService, ProductSyncService productSyncService) {
        this.productService = productService;
        this.productSyncService = productSyncService;
    }

    @GetMapping
    public ResponseEntity<PageDTO<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PageDTO<ProductDTO> products = productService.getAllProducts(pageable, name, minPrice, maxPrice, categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageDTO<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProducts(pageable, null, null, null, categoryId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<AutocompleteResponse> getAutocompleteSuggestions(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getAutocompleteSuggestions(query, limit));
    }

    @PostMapping("/sync-to-elasticsearch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> syncToElasticsearch() {
        productSyncService.syncAllProducts();
        return ResponseEntity.ok("Product sync to Elasticsearch completed successfully");
    }
} 