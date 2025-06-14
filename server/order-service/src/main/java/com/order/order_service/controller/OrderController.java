package com.order.order_service.controller;

import com.order.order_service.DTOs.ApiResponse;
import com.order.order_service.DTOs.OrderRequestDTO;
import com.order.order_service.DTOs.OrderResponseDTO;
import com.order.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Endpoints for creating, querying, and managing orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order with the given details",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Parameter(description = "Order request payload", required = true)
            @Valid @RequestBody OrderRequestDTO orderRequest
    ) {
        OrderResponseDTO orderResponse = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order processing", orderResponse));
    }

    @Operation(
            summary = "Get order by ID",
            description = "Retrieves the details of a specific order by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true, example = "123")
            @PathVariable Long id
    ) {
        OrderResponseDTO orderResponse = orderService.getOrderById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order details", orderResponse));
    }

    @Operation(
            summary = "List orders by user ID",
            description = "Retrieves all orders placed by a given user"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersByUserId(
            @Parameter(description = "ID of the user", required = true, example = "user-456")
            @PathVariable String userId
    ) {
        List<OrderResponseDTO> orderResponses = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders by user ID", orderResponses));
    }

    @Operation(
            summary = "List orders by status",
            description = "Retrieves all orders filtered by their current status"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersByStatus(
            @Parameter(description = "Status to filter orders by", required = true, example = "PENDING")
            @PathVariable String status
    ) {
        List<OrderResponseDTO> orderResponses = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders by status", orderResponses));
    }

    @Operation(
            summary = "Update order status",
            description = "Updates the status of an existing order (admin only)"
    )
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(
            @Parameter(description = "ID of the order to update", required = true, example = "123")
            @PathVariable Long id,
            @Parameter(description = "New status value", required = true, example = "SHIPPED")
            @RequestParam String status
    ) {
        OrderResponseDTO orderResponse = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated", orderResponse));
    }

    @Operation(
            summary = "Delete an order",
            description = "Deletes an existing order by its ID (admin only)"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @Parameter(description = "ID of the order to delete", required = true, example = "123")
            @PathVariable Long id
    ) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order deleted", null));
    }

    @Operation(
            summary = "List all orders",
            description = "Retrieves all orders in the system (admin only)"
    )
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
        List<OrderResponseDTO> orderResponses = orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse<>(true, "All orders retrieved", orderResponses));
    }

    @Operation(
            summary = "Update delivery status",
            description = "Updates the delivery status of an order (admin only)"
    )
    @PatchMapping("/admin/{id}/delivery-status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateDeliveryStatus(
            @Parameter(description = "ID of the order", required = true, example = "123")
            @PathVariable Long id,
            @Parameter(description = "New delivery status", required = true, example = "DELIVERED")
            @RequestParam String deliveryStatus
    ) {
        OrderResponseDTO orderResponse = orderService.updateDeliveryStatus(id, deliveryStatus);
        return ResponseEntity.ok(new ApiResponse<>(true, "Delivery status updated", orderResponse));
    }
}
