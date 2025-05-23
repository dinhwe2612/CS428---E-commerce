package com.catalog.catalog_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/internal/products")
@Tag(name = "Internal Product Management", description = "Internal APIs for product management")
public class ProductControllerInternal {

    private final ProductService productService;

    @Autowired
    public ProductControllerInternal(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get product by ID (Internal API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

}
