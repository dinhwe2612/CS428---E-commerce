package com.order.order_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutOfStockEvent {
    private Long orderId;
    private String productId;
    private String reason;
} 