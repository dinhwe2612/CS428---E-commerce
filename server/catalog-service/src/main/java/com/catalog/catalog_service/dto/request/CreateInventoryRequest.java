package com.catalog.catalog_service.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInventoryRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Current stock is required")
    @PositiveOrZero(message = "Current stock cannot be negative")
    private Integer currentStock;

    @NotNull(message = "Available stock is required")
    @PositiveOrZero(message = "Available stock cannot be negative")
    private Integer availableStock;

    @PositiveOrZero(message = "Reserved quantity cannot be negative")
    private Integer reservedQuantity;

    @NotNull(message = "Reorder level is required")
    @PositiveOrZero(message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @NotNull(message = "Reorder quantity is required")
    @Positive(message = "Reorder quantity must be greater than 0")
    private Integer reorderQuantity;

    @NotNull(message = "Low stock threshold is required")
    @PositiveOrZero(message = "Low stock threshold cannot be negative")
    private Integer lowStockThreshold;

    @Positive(message = "Unit cost must be greater than 0")
    private BigDecimal unitCost;

    private String location;
    private String status;
    private Long supplierId;
} 