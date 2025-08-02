package com.order.order_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guest_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Guest Order Item entity representing an item in a guest order")
public class GuestOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the guest order item", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guest_order_id", nullable = false)
    @Schema(description = "The guest order this item belongs to")
    private GuestOrder guestOrder;

    @Column(nullable = false)
    @Schema(description = "Product ID", example = "123")
    private String productId;

    @Column(nullable = false)
    @Schema(description = "Product name", example = "Rose Bouquet")
    private String productName;

    @Column(nullable = false)
    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Column(nullable = false)
    @Schema(description = "Unit price", example = "29.99")
    private Double unitPrice;

    @Column(nullable = false)
    @Schema(description = "Subtotal for this item", example = "59.98")
    private Double subtotal;

    @Column(nullable = false)
    @Schema(description = "Inventory ID", example = "inv_456")
    private String inventoryId;
} 