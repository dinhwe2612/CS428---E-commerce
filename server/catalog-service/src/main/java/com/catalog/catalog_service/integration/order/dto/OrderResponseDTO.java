package com.catalog.catalog_service.integration.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    
    @JsonProperty("user_id")
    private String userId;
    
    private String status;
    
    @JsonProperty("total_amount")
    private Double totalAmount;
    
    @JsonProperty("shipping_address")
    private String shippingAddress;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("order_items")
    private List<OrderItemResponseDTO> orderItems;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    private String message;
} 