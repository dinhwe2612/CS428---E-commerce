package com.payment.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.payment.payment_service.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    
    @NotNull(message = "Order ID is required")
    @JsonProperty("order_id")
    private Long orderId;
    
    @NotNull(message = "Return URL is required")
    @JsonProperty("return_url")
    private String returnUrl;
    
    @NotNull(message = "Cancel URL is required")
    @JsonProperty("cancel_url")
    private String cancelUrl;
} 