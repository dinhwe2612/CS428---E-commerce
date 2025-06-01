package com.order.order_service.controller;

import com.order.order_service.DTOs.OrderResponseDTO;
import com.order.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/orders")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderService orderService;
    
    @Value("${INTERNAL_API_KEY}")
    private String internalApiKey;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable Long id,
            @RequestHeader("X-Internal-Api-Key") String apiKey) {

        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(401).build();
        }
        
        OrderResponseDTO order = orderService.getOrderById(id);
        System.out.println("Order: " + order);
        return ResponseEntity.ok(order);
    }
} 