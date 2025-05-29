package com.catalog.catalog_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.service.ProductService;

@RestController
@RequestMapping("/api/v1/internal/products")
public class ProductControllerInternal {

    private final ProductService productService;

    @Autowired
    public ProductControllerInternal(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

}
