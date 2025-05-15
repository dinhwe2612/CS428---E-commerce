package com.catalog.catalog_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInventoryRequest {
    @PositiveOrZero(message = "Current stock cannot be negative")
    private Integer currentStock;

    @PositiveOrZero(message = "Available stock cannot be negative")
    private Integer availableStock;

    @PositiveOrZero(message = "Reserved quantity cannot be negative")
    private Integer reservedQuantity;

    @PositiveOrZero(message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @Positive(message = "Reorder quantity must be greater than 0")
    private Integer reorderQuantity;

    @PositiveOrZero(message = "Low stock threshold cannot be negative")
    private Integer lowStockThreshold;

    @Positive(message = "Unit cost must be greater than 0")
    private BigDecimal unitCost;

    private String location;
    private String status;
    private Long supplierId;
} 