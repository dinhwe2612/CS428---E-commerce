package com.catalog.catalog_service.controller;

import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.CreateInventoryRequest;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.service.InventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventories")
@Tag(name = "Inventories", description = "Endpoints for managing product inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(
            summary = "List all inventories",
            description = "Retrieves a list of all inventory records",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventories retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = InventoryDTO.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @Operation(
            summary = "Get inventory by ID",
            description = "Retrieves a single inventory record by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = InventoryDTO.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventoryById(
            @Parameter(description = "ID of the inventory record", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @Operation(
            summary = "List inventories by product ID",
            description = "Retrieves all inventory records associated with a given product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventories retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = InventoryDTO.class))))
            }
    )
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryDTO>> getInventoryByProductId(
            @Parameter(description = "Product ID to filter by", required = true, example = "100")
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(inventoryService.getInventoriesByProductId(productId));
    }

    @Operation(
            summary = "Create a new inventory record",
            description = "Creates an inventory entry for a product",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "CreateInventoryRequest payload",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateInventoryRequest.class)))
    )
    @ApiResponse(responseCode = "200", description = "Inventory created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = InventoryDTO.class)))
    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(
            @Parameter(hidden = true)
            @RequestBody CreateInventoryRequest request
    ) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @Operation(
            summary = "Update an inventory record",
            description = "Updates an existing inventory entry by ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "UpdateInventoryRequest payload",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateInventoryRequest.class)))
    )
    @ApiResponse(responseCode = "200", description = "Inventory updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = InventoryDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> updateInventory(
            @Parameter(description = "ID of the inventory record to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestBody UpdateInventoryRequest request
    ) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request));
    }

    @Operation(
            summary = "Delete an inventory record",
            description = "Deletes a specific inventory entry by its ID"
    )
    @ApiResponse(responseCode = "204", description = "Inventory deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "ID of the inventory record to delete", required = true, example = "1")
            @PathVariable Long id
    ) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
