package com.microservice_ecommerce.cart.interfaces.controller;

import com.microservice_ecommerce.cart.application.dto.ApiResponse;
import com.microservice_ecommerce.cart.application.service.CartService;
import com.microservice_ecommerce.cart.interfaces.dto.request.AddItemRequestDTO;
import com.microservice_ecommerce.cart.interfaces.dto.response.CartResponseDTO;
import com.microservice_ecommerce.cart.interfaces.mapper.CartMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    
    private final CartService cartService;
    private final CartMapper cartMapper;
    
    @Autowired
    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart() {
        CartResponseDTO cart = cartMapper.toCartResponseDTO(cartService.getCart());
        ApiResponse<CartResponseDTO> response = new ApiResponse<>(true, "Cart retrieved successfully", cart);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addItemToCart(@RequestBody AddItemRequestDTO requestDTO) {
        CartResponseDTO cart = cartMapper.toCartResponseDTO(
            cartService.addItemToCart(cartMapper.toCartItemDTO(requestDTO))
        );
        ApiResponse<CartResponseDTO> response = new ApiResponse<>(true, "Item added to cart successfully", cart);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeItemFromCart(@PathVariable UUID itemId) {
        CartResponseDTO cart = cartMapper.toCartResponseDTO(cartService.removeItemFromCart(itemId));
        ApiResponse<CartResponseDTO> response = new ApiResponse<>(true, "Item removed from cart successfully", cart);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        cartService.clearCart();
        ApiResponse<Void> response = new ApiResponse<>(true, "Cart cleared successfully", null);
        return ResponseEntity.ok(response);
    }
} 