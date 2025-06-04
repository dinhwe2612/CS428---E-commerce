package com.catalog.catalog_service.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.request.ProductSearchRequest;
import com.catalog.catalog_service.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/products")
public class ProductControllerInternal {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageDTO<ProductDTO>> searchProducts(@ModelAttribute ProductSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ResponseEntity.ok(productService.getAllProducts(pageable, request.getName(), 
                request.getMinPrice(), request.getMaxPrice(), request.getCategoryId()));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> getProductsByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageDTO<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProducts(pageable, null, null, null, categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

}
