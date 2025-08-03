package com.order.order_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.order.order_service.DTOs.ApiResponse;
import com.order.order_service.DTOs.GuestOrderRequestDTO;
import com.order.order_service.DTOs.GuestOrderResponseDTO;
import com.order.order_service.service.GuestOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/guest-orders")
@Tag(name = "Guest Orders", description = "Endpoints for creating, querying, and managing guest orders")
@RequiredArgsConstructor
public class GuestOrderController {

    private final GuestOrderService guestOrderService;

    @Operation(
            summary = "Create a guest order",
            description = "Creates a new order for unregistered users",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Guest order created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<GuestOrderResponseDTO>> createGuestOrder(
            @Parameter(description = "Guest order request payload", required = true)
            @Valid @RequestBody GuestOrderRequestDTO orderRequest
    ) {
        GuestOrderResponseDTO orderResponse = guestOrderService.createGuestOrder(orderRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest order processing", orderResponse));
    }

    @Operation(
            summary = "Get guest order by ID",
            description = "Retrieves the details of a specific guest order by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GuestOrderResponseDTO>> getGuestOrderById(
            @Parameter(description = "ID of the guest order to retrieve", required = true, example = "123")
            @PathVariable Long id
    ) {
        GuestOrderResponseDTO orderResponse = guestOrderService.getGuestOrderById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest order details", orderResponse));
    }

    @Operation(
            summary = "List guest orders by status",
            description = "Retrieves all guest orders filtered by their current status"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<GuestOrderResponseDTO>>> getGuestOrdersByStatus(
            @Parameter(description = "Status to filter guest orders by", required = true, example = "PENDING")
            @PathVariable String status
    ) {
        List<GuestOrderResponseDTO> orderResponses = guestOrderService.getGuestOrdersByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest orders by status", orderResponses));
    }

    @Operation(
            summary = "List guest orders by email",
            description = "Retrieves all guest orders placed by a given email"
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<List<GuestOrderResponseDTO>>> getGuestOrdersByEmail(
            @Parameter(description = "Email of the customer", required = true, example = "john@example.com")
            @PathVariable String email
    ) {
        List<GuestOrderResponseDTO> orderResponses = guestOrderService.getGuestOrdersByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest orders by email", orderResponses));
    }

    @Operation(
            summary = "Update guest order status",
            description = "Updates the status of an existing guest order (admin only)"
    )
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<GuestOrderResponseDTO>> updateGuestOrderStatus(
            @Parameter(description = "ID of the guest order to update", required = true, example = "123")
            @PathVariable Long id,
            @Parameter(description = "New status value", required = true, example = "SHIPPED")
            @RequestParam String status
    ) {
        GuestOrderResponseDTO orderResponse = guestOrderService.updateGuestOrderStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest order status updated", orderResponse));
    }

    @Operation(
            summary = "Update guest order delivery status",
            description = "Updates the delivery status of a guest order (admin only)"
    )
    @PatchMapping("/admin/{id}/delivery-status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<GuestOrderResponseDTO>> updateGuestOrderDeliveryStatus(
            @Parameter(description = "ID of the guest order", required = true, example = "123")
            @PathVariable Long id,
            @Parameter(description = "New delivery status", required = true, example = "DELIVERED")
            @RequestParam String deliveryStatus
    ) {
        GuestOrderResponseDTO orderResponse = guestOrderService.updateGuestOrderDeliveryStatus(id, deliveryStatus);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest order delivery status updated", orderResponse));
    }

    @Operation(
            summary = "Get all guest orders",
            description = "Retrieves all guest orders in the system (admin only)"
    )
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<GuestOrderResponseDTO>>> getAllGuestOrders() {
        List<GuestOrderResponseDTO> orderResponses = guestOrderService.getAllGuestOrders();
        return ResponseEntity.ok(new ApiResponse<>(true, "All guest orders retrieved", orderResponses));
    }
} 