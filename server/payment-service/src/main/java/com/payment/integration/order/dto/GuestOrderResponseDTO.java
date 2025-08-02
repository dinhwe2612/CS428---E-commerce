package com.payment.integration.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderResponseDTO {
    private Long id;
    
    @JsonProperty("customer_name")
    private String customerName;
    
    @JsonProperty("customer_email")
    private String customerEmail;
    
    @JsonProperty("customer_phone")
    private String customerPhone;
    
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
    
    @JsonProperty("transaction_id")
    private String transactionId;
    
    @JsonProperty("payment_url")
    private String paymentUrl;
    
    @JsonProperty("delivery_status")
    private String deliveryStatus;
} 