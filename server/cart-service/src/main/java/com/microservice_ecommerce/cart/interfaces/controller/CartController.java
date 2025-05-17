package com.microservice_ecommerce.cart.interfaces.controller;

import com.microservice_ecommerce.cart.application.dto.CartDTO;
import com.microservice_ecommerce.cart.application.dto.CartItemDTO;
import com.microservice_ecommerce.cart.application.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private final CartService cartService;
    
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    @PostMapping
    public ResponseEntity<CartDTO> createCart(@RequestParam UUID userId) {
        return ResponseEntity.ok(cartService.createCart(userId));
    }
    
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable UUID cartId) {
        return ResponseEntity.ok(cartService.getCart(cartId));
    }
    
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartDTO> addItemToCart(@PathVariable UUID cartId, @RequestBody CartItemDTO itemDTO) {
        return ResponseEntity.ok(cartService.addItemToCart(cartId, itemDTO));
    }
    
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable UUID cartId, @PathVariable UUID itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(cartId, itemId));
    }
    
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable UUID cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
} 