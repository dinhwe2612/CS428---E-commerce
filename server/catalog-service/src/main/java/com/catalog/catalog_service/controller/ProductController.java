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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Endpoints for managing products and search")
public class ProductController {

    private final ProductService productService;
    private final ProductSyncService productSyncService;

    @Autowired
    public ProductController(ProductService productService,
                             ProductSyncService productSyncService) {
        this.productService = productService;
        this.productSyncService = productSyncService;
    }

    @Operation(
            summary = "List products with pagination & filters",
            description = "Retrieves a paginated list of products, optionally filtering by name, price range, or category, and sorting."
    )
    @ApiResponse(responseCode = "200", description = "Page of products returned",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PageDTO.class)))
    @GetMapping
    public ResponseEntity<PageDTO<ProductDTO>> getAllProducts(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")          @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by product name")            @RequestParam(required = false) String name,
            @Parameter(description = "Minimum price", example = "0.0")     @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price", example = "100.0")   @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Category ID to filter by", example = "1")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Field to sort by", example = "name") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc|desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageDTO<ProductDTO> products = productService.getAllProducts(
                pageable, name, minPrice, maxPrice, categoryId);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its ID")
    @ApiResponse(responseCode = "200", description = "Product returned",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID of the product", required = true, example = "1") @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Create a product", description = "Creates a new product (admin only)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product creation payload",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreateProductRequest.class))
    )
    @ApiResponse(responseCode = "200", description = "Product created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class)))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestBody CreateProductRequest request
    ) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Operation(summary = "Update a product", description = "Updates an existing product (admin only)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product update payload",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UpdateProductRequest.class))
    )
    @ApiResponse(responseCode = "200", description = "Product updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class)))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID of the product", required = true, example = "1") @PathVariable Long id,
            @RequestBody UpdateProductRequest request
    ) {
        System.out.println("=== UPDATE PRODUCT REQUEST ===");
        System.out.println("Product ID: " + id);
        System.out.println("Request: " + request);
        try {
            ProductDTO result = productService.updateProduct(id, request);
            System.out.println("=== UPDATE PRODUCT SUCCESS ===");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("=== UPDATE PRODUCT ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Operation(summary = "Delete a product", description = "Deletes a product by ID (admin only)")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product", required = true, example = "1") @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List products by category", description = "Retrieves products filtered by category with pagination")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageDTO<ProductDTO>> getProductsByCategory(
            @Parameter(description = "Category ID", required = true, example = "1") @PathVariable Long categoryId,
            @Parameter(description = "Page number", example = "0")         @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")          @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                productService.getAllProducts(pageable, null, null, null, categoryId)
        );
    }

    @Operation(summary = "List all products", description = "Retrieves all products without pagination")
    @ApiResponse(responseCode = "200", description = "List of all products",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))))
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @Operation(summary = "Autocomplete product names", description = "Provides search suggestions for product names")
    @ApiResponse(responseCode = "200", description = "Autocomplete suggestions",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AutocompleteResponse.class)))
    @GetMapping("/autocomplete")
    public ResponseEntity<AutocompleteResponse> getAutocompleteSuggestions(
            @Parameter(description = "Search query", example = "phone")       @RequestParam(required = false) String query,
            @Parameter(description = "Max suggestions", example = "10")       @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(productService.getAutocompleteSuggestions(query, limit));
    }

    @Operation(summary = "Sync products to Elasticsearch", description = "Triggers a full sync of products to Elasticsearch (admin only)")
    @ApiResponse(responseCode = "200", description = "Sync completed")
    @PostMapping("/sync-to-elasticsearch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> syncToElasticsearch() {
        productSyncService.syncAllProducts();
        return ResponseEntity.ok("Product sync to Elasticsearch completed successfully");
    }
}
