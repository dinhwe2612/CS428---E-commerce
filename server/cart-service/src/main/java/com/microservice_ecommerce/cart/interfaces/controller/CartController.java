package com.microservice_ecommerce.cart.interfaces.controller;

import com.microservice_ecommerce.cart.application.dto.ApiResponse;
import com.microservice_ecommerce.cart.application.service.CartService;
import com.microservice_ecommerce.cart.interfaces.dto.request.AddItemRequestDTO;
import com.microservice_ecommerce.cart.interfaces.dto.response.CartResponseDTO;
import com.microservice_ecommerce.cart.interfaces.mapper.CartMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart", description = "Endpoints for retrieving and modifying the shopping cart")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @Autowired
    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @Operation(
            summary = "Get current cart",
            description = "Retrieves the current user's shopping cart",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.microservice_ecommerce.cart.application.dto.ApiResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart() {
        CartResponseDTO cart = cartMapper.toCartResponseDTO(cartService.getCart());
        ApiResponse<CartResponseDTO> response = new ApiResponse<>(true, "Cart retrieved successfully", cart);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add item to cart",
            description = "Adds an item to the shopping cart",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Item details to add", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddItemRequestDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item added successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.microservice_ecommerce.cart.application.dto.ApiResponse.class)))
            }
    )
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addItemToCart(
            @Parameter(hidden = true) @RequestBody AddItemRequestDTO requestDTO
    ) {
        CartResponseDTO cart = cartMapper.toCartResponseDTO(
                cartService.addItemToCart(cartMapper.toCartItemDTO(requestDTO))
        );
        ApiResponse<CartResponseDTO> response = new ApiResponse<>(true, "Item added to cart successfully", cart);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Remove item from cart",
            description = "Removes a specific item from the shopping cart",
            parameters = {
                    @Parameter(name = "itemId", description = "UUID of the item to remove", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item removed successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.microservice_ecommerce.cart.application.dto.ApiResponse.class)))
            }
    )
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeItemFromCart(
            @PathVariable UUID itemId
    ) {
        CartResponseDTO cart = cartMapper.toCartResponseDTO(cartService.removeItemFromCart(itemId));
        ApiResponse<CartResponseDTO> response = new ApiResponse<>(true, "Item removed from cart successfully", cart);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Clear cart",
            description = "Removes all items from the shopping cart",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart cleared successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = com.microservice_ecommerce.cart.application.dto.ApiResponse.class)))
            }
    )
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        cartService.clearCart();
        ApiResponse<Void> response = new ApiResponse<>(true, "Cart cleared successfully", null);
        return ResponseEntity.ok(response);
    }
}
