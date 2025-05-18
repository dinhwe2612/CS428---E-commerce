package com.order.order_service.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private String userId;
    private String shippingAddress;
    private String paymentMethod;
    private List<OrderItemRequestDTO> orderItems;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class OrderItemRequestDTO {
    private String productId;
    private Integer quantity;
} 