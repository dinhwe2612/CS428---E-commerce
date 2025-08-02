package com.order.order_service.DTOs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderRequestDTO {
    
    @NotBlank(message = "Customer name is required")
    @JsonProperty("customer_name")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("customer_email")
    private String customerEmail;
    
    @NotBlank(message = "Customer phone is required")
    @JsonProperty("customer_phone")
    private String customerPhone;
    
    @NotBlank(message = "Shipping address is required")
    @JsonProperty("shipping_address")
    private String shippingAddress;
    
    @NotNull(message = "Payment method is required")
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @NotNull(message = "Total amount is required")
    @JsonProperty("total_amount")
    private Double totalAmount;
    
    @NotNull(message = "Order items are required")
    @JsonProperty("order_items")
    private List<OrderItemRequestDTO> orderItems;
    
    @JsonProperty("message")
    private String message;
} 