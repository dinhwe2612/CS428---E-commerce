package com.catalog.catalog_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.service.InventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/inventories")
@Tag(name = "Inventory Management", description = "APIs for managing product inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Get all inventories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all inventories")
    })
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @Operation(summary = "Get inventory by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the inventory"),
        @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventoryById(
            @Parameter(description = "Inventory ID") @PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @Operation(summary = "Get inventory by product ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the inventory"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @Operation(summary = "Create a new inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created the inventory"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(
            @Parameter(description = "Inventory creation data") @RequestBody CreateInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @Operation(summary = "Update an existing inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the inventory"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> updateInventory(
            @Parameter(description = "Inventory ID") @PathVariable Long id,
            @Parameter(description = "Inventory update data") @RequestBody UpdateInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request));
    }

    @Operation(summary = "Delete an inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the inventory"),
        @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "Inventory ID") @PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
} 