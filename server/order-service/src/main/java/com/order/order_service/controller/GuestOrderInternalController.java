package com.order.order_service.controller;

import com.order.order_service.DTOs.GuestOrderResponseDTO;
import com.order.order_service.service.GuestOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/guest-orders")
@RequiredArgsConstructor
public class GuestOrderInternalController {

    private final GuestOrderService guestOrderService;
    
    @Value("${INTERNAL_API_KEY}")
    private String internalApiKey;

    @GetMapping("/{id}")
    public ResponseEntity<GuestOrderResponseDTO> getGuestOrderById(
            @PathVariable Long id,
            @RequestHeader("X-Internal-Api-Key") String apiKey) {

        System.out.println("Expected API Key: " + internalApiKey);
        System.out.println("Received API Key: " + apiKey);
        
        if (!internalApiKey.equals(apiKey)) {
            System.out.println("API Key mismatch!");
            return ResponseEntity.status(401).build();
        }
        
        GuestOrderResponseDTO order = guestOrderService.getGuestOrderById(id);
        System.out.println("Guest Order: " + order);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<List<GuestOrderResponseDTO>> getGuestOrdersByEmail(
            @PathVariable String email,
            @RequestHeader("X-Internal-Api-Key") String apiKey) {

        System.out.println("Expected API Key: " + internalApiKey);
        System.out.println("Received API Key: " + apiKey);
        
        if (!internalApiKey.equals(apiKey)) {
            System.out.println("API Key mismatch!");
            return ResponseEntity.status(401).build();
        }
        
        List<GuestOrderResponseDTO> orders = guestOrderService.getGuestOrdersByEmail(email);
        System.out.println("Guest Orders by email: " + orders);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<GuestOrderResponseDTO>> getGuestOrdersByStatus(
            @PathVariable String status,
            @RequestHeader("X-Internal-Api-Key") String apiKey) {

        System.out.println("Expected API Key: " + internalApiKey);
        System.out.println("Received API Key: " + apiKey);
        
        if (!internalApiKey.equals(apiKey)) {
            System.out.println("API Key mismatch!");
            return ResponseEntity.status(401).build();
        }
        
        List<GuestOrderResponseDTO> orders = guestOrderService.getGuestOrdersByStatus(status);
        System.out.println("Guest Orders by status: " + orders);
        return ResponseEntity.ok(orders);
    }
} 