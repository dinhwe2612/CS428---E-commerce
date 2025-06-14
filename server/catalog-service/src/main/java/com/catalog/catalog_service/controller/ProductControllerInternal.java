package com.catalog.catalog_service.controller;

import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.request.ProductSearchRequest;
import com.catalog.catalog_service.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/products")
@Tag(name = "Internal Products", description = "Internal endpoints for searching and retrieving products")
@RequiredArgsConstructor
public class ProductControllerInternal {

    private final ProductService productService;

    @Operation(
            summary = "Search products (internal)",
            description = "Searches products with pagination and filters (all request parameters in snake_case)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of products returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageDTO.class)))
            }
    )
    @GetMapping
    public ResponseEntity<PageDTO<ProductDTO>> searchProducts(
            @Parameter(hidden = true)
            @ModelAttribute ProductSearchRequest request
    ) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ResponseEntity.ok(
                productService.getAllProducts(
                        pageable,
                        request.getName(),
                        request.getMinPrice(),
                        request.getMaxPrice(),
                        request.getCategoryId()
                )
        );
    }

    @Operation(
            summary = "Get products by IDs (internal)",
            description = "Retrieves a list of products by their IDs",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of products returned",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))))
            }
    )
    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> getProductsByIds(
            @Parameter(description = "Comma-separated list of product IDs", required = true, example = "1,2,3")
            @RequestParam List<Long> ids
    ) {
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }

    @Operation(
            summary = "Get all products (internal)",
            description = "Retrieves all products without pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of all products returned",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))))
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @Operation(
            summary = "Get products by category (internal)",
            description = "Retrieves products filtered by category with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Page of products by category returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageDTO.class)))
            }
    )
    @GetMapping("/category/{category_id}")
    public ResponseEntity<PageDTO<ProductDTO>> getProductsByCategory(
            @Parameter(description = "Category ID to filter by", required = true, example = "5")
            @PathVariable("category_id") Long categoryId,
            @Parameter(description = "Page number (snake_case)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size (snake_case)", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                productService.getAllProducts(pageable, null, null, null, categoryId)
        );
    }

    @Operation(
            summary = "Get product by ID (internal)",
            description = "Retrieves a single product by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDTO.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
