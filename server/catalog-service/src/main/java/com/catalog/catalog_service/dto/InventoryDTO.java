package com.catalog.catalog_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    private Long id;
    private Long productId;
    private Integer currentStock;
    private Integer availableStock;
    private Integer reservedQuantity;
    private Integer reorderLevel;
    private Integer reorderQuantity;
    private Integer lowStockThreshold;
    private BigDecimal unitCost;
    private String location;
    private String status;
    private LocalDateTime lastStockMovement;
    private String lastMovementType;
    private Integer lastMovementQuantity;
    private Long supplierId;
} 