package com.payment.payment_service.dto;

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
public class GuestPaymentRequestDTO {
    
    @NotNull(message = "Order ID is required")
    @JsonProperty("order_id")
    private Long orderId;
    
    @NotNull(message = "Return URL is required")
    @JsonProperty("return_url")
    private String returnUrl;
    
    @NotNull(message = "Cancel URL is required")
    @JsonProperty("cancel_url")
    private String cancelUrl;
    
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
    
    @JsonProperty("customer_address")
    private String customerAddress;
} 