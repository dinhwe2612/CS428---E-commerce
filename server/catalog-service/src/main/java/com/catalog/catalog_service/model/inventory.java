package com.catalog.catalog_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique identifier for the inventory record
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private product product;  // Bidirectional relationship with product
    
    @Version
    private Long version;
    
    private int currentStock;  // Total physical stock available in the warehouse
    private int availableStock;  // Stock available for sale (currentStock - reservedQuantity)
    private int lowStockThreshold;  // Minimum stock level before triggering low stock alerts
    private int reorderLevel;  // Stock level at which new orders should be placed
    private int reorderQuantity;  // Quantity to order when reaching reorder level
    private int reservedQuantity;  // Stock reserved for pending orders or in shopping carts
    private String location;  // Physical location or warehouse where the stock is stored
    private BigDecimal unitCost;  // Cost per unit for inventory valuation
    private Long supplierId;  // Reference to the supplier for reordering
    private String status;  // Current status (e.g., ACTIVE, DISCONTINUED, ON_HOLD)
    private LocalDateTime lastUpdated;  // Timestamp of the last inventory update
    private LocalDateTime lastStockMovement;  // Timestamp of the most recent stock movement
    private String lastMovementType;  // Type of last movement (PURCHASE, SALE, ADJUSTMENT)
    private int lastMovementQuantity;  // Quantity involved in the last stock movement
}
