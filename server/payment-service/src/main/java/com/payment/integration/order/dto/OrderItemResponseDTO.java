package com.payment.integration.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long id;
    
    @JsonProperty("product_id")
    private String productId;
    
    @JsonProperty("product_name")
    private String productName;
    
    private Integer quantity;
    
    @JsonProperty("unit_price")
    private Double unitPrice;
    
    private Double subtotal;
    
    @JsonProperty("inventory_id")
    private String inventoryId;
} 