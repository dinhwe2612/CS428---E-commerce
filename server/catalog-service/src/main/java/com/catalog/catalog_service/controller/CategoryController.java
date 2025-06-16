package com.catalog.catalog_service.controller;

import com.catalog.catalog_service.dto.CategoryDTO;
import com.catalog.catalog_service.dto.request.CreateCategoryRequest;
import com.catalog.catalog_service.dto.request.UpdateCategoryRequest;
import com.catalog.catalog_service.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Endpoints for managing product categories")
public class CategoryController {
        // TODO: add pagination
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "List all categories",
            description = "Retrieves a list of all product categories",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CategoryDTO.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a single category by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryDTO.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "ID of the category", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(
            summary = "Create a new category",
            description = "Creates a new product category (admin only). Request fields use snake_case.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "CreateCategoryRequest in snake_case, e.g. { \"category_name\": \"Electronics\" }",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateCategoryRequest.class)))
    )
    @ApiResponse(responseCode = "200", description = "Category created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class)))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(
            @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @Operation(
            summary = "Update an existing category",
            description = "Updates fields of a category by ID (admin only). Request fields use snake_case.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "UpdateCategoryRequest in snake_case, e.g. { \"category_name\": \"New Name\" }",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateCategoryRequest.class)))
    )
    @ApiResponse(responseCode = "200", description = "Category updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class)))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "ID of the category to update", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @Operation(
            summary = "Delete a category",
            description = "Deletes a category by its ID (admin only)"
    )
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true, example = "1")
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
