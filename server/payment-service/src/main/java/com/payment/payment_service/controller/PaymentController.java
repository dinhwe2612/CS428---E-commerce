package com.payment.payment_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.payment.payment_service.dto.ApiResponse;
import com.payment.payment_service.dto.PaymentRequestDTO;
import com.payment.payment_service.dto.PaymentResponseDTO;
import com.payment.payment_service.dto.GuestPaymentRequestDTO;
import com.payment.payment_service.model.PaymentStatus;
import com.payment.payment_service.security.UserDetailsWithUserId;
import com.payment.payment_service.service.PaymentService;
import com.payment.integration.order.client.OrderServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Operations for creating and querying payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderServiceClient orderServiceClient;

    @Operation(
            summary = "Create a new payment",
            description = "Creates a payment for an order on behalf of the authenticated user",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment created successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> createPayment(
            @Parameter(description = "Payment request payload", required = true)
            @Valid @RequestBody PaymentRequestDTO paymentRequest,
            Authentication authentication
    ) {
        String userId = extractUserIdFromAuthentication(authentication);
        PaymentResponseDTO response = paymentService.createPayment(paymentRequest, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment created successfully", response));
    }

    @Operation(
            summary = "Create a guest payment",
            description = "Creates a payment for an order for unregistered users",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Guest payment created successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> createGuestPayment(
            @Parameter(description = "Guest payment request payload", required = true)
            @Valid @RequestBody GuestPaymentRequestDTO paymentRequest
    ) {
        PaymentResponseDTO response = paymentService.createGuestPayment(paymentRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest payment created successfully", response));
    }

    @Operation(summary = "Get payment by ID", description = "Retrieve a single payment by its database ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentById(
            @Parameter(description = "ID of the payment", required = true, example = "42")
            @PathVariable Long id
    ) {
        PaymentResponseDTO response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved successfully", response));
    }

    @Operation(summary = "Get payment by transaction ID", description = "Fetch a payment using its external transaction identifier")
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentByTransactionId(
            @Parameter(description = "External transaction ID", required = true, example = "tx_123abc")
            @PathVariable String transactionId
    ) {
        PaymentResponseDTO response = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved successfully", response));
    }

    @Operation(summary = "List payments for an order", description = "Returns all payments associated with the given order ID")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPaymentsByOrderId(
            @Parameter(description = "Order ID", required = true, example = "1001")
            @PathVariable Long orderId
    ) {
        List<PaymentResponseDTO> responses = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", responses));
    }

    @Operation(summary = "List my payments", description = "Returns all payments made by the authenticated user")
    @GetMapping("/user/my-payments")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getMyPayments(
            Authentication authentication
    ) {
        String userId = extractUserIdFromAuthentication(authentication);
        List<PaymentResponseDTO> responses = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", responses));
    }
    
    @Operation(summary = "List guest payments by email", description = "Returns all payments made by a guest user using their email")
    @GetMapping("/guest/email/{email}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getGuestPaymentsByEmail(
            @Parameter(description = "Guest email", required = true, example = "guest@example.com")
            @PathVariable String email
    ) {
        List<PaymentResponseDTO> responses = paymentService.getGuestPaymentsByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest payments retrieved successfully", responses));
    }

    @Operation(summary = "List payments by status", description = "Filter payments by their current status")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPaymentsByStatus(
            @Parameter(description = "Payment status", required = true)
            @PathVariable PaymentStatus status
    ) {
        List<PaymentResponseDTO> responses = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", responses));
    }

    @Operation(summary = "Update payment status", description = "Change the status of a payment by its transaction ID")
    @PutMapping("/transaction/{transactionId}/status")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> updatePaymentStatus(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable String transactionId,
            @Parameter(description = "New status", required = true)
            @RequestParam PaymentStatus status
    ) {
        PaymentResponseDTO response = paymentService.updatePaymentStatus(transactionId, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment status updated successfully", response));
    }

    @Operation(summary = "Cancel a payment", description = "Mark a payment as cancelled")
    @PostMapping("/transaction/{transactionId}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> cancelPayment(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable String transactionId
    ) {
        PaymentResponseDTO response = paymentService.cancelPayment(transactionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment cancelled successfully", response));
    }

    @Operation(summary = "Refund a payment", description = "Issue a refund for a payment with an optional reason")
    @PostMapping("/transaction/{transactionId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> refundPayment(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable String transactionId,
            @Parameter(description = "Reason for refund", example = "Customer request")
            @RequestParam(required = false, defaultValue = "User requested refund") String reason
    ) {
        PaymentResponseDTO response = paymentService.refundPayment(transactionId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment refunded successfully", response));
    }

    @Operation(summary = "Handle PayOS webhook", description = "Receive and process webhook callbacks from PayOS")
    @PostMapping("/webhook/payos")
    public ResponseEntity<ObjectNode> handlePayOSWebhook(
            @Parameter(hidden = true)
            @RequestBody Map<String, Object> body
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            log.info("Received PayOS webhook: {}", body);
            paymentService.processWebhook(body);
            response.put("error", 0).put("message", "Webhook processed successfully");
            response.set("data", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing PayOS webhook: {}", e.getMessage(), e);
            response.put("error", -1).put("message", e.getMessage());
            response.set("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "Payment success callback", description = "Endpoint PayOS redirects to on payment success")
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<String>> handlePaymentSuccess(
            @Parameter(description = "Authorization code", required = true) @RequestParam String code,
            @Parameter(description = "Payment ID", required = true)         @RequestParam String id,
            @Parameter(description = "Order Code", example = "1001")        @RequestParam Long orderCode,
            @Parameter(description = "Status", required = false)            @RequestParam(required = false) String status
    ) {
        try {
            log.info("Received payment success callback - code: {}, id: {}, orderCode: {}, status: {}",
                    code, id, orderCode, status);
            String result = paymentService.processReturnUrl(code, id, orderCode, status, true);
            return ResponseEntity.ok(new ApiResponse<>(true, "Payment processed successfully", result));
        } catch (Exception e) {
            log.error("Error processing success callback: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error processing payment: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "Payment cancellation callback", description = "Endpoint PayOS redirects to on payment cancellation")
    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> handlePaymentCancel(
            @Parameter(description = "Authorization code", required = true) @RequestParam String code,
            @Parameter(description = "Payment ID", required = true)         @RequestParam String id,
            @Parameter(description = "Order Code", example = "1001")        @RequestParam Long orderCode,
            @Parameter(description = "Status", required = false)            @RequestParam(required = false) String status,
            @Parameter(description = "Cancel flag", example = "true")       @RequestParam(required = false, defaultValue = "false") boolean cancel
    ) {
        try {
            log.info("Received payment cancel callback - code: {}, id: {}, orderCode: {}, status: {}, cancel: {}",
                    code, id, orderCode, status, cancel);
            String result = paymentService.processReturnUrl(code, id, orderCode, status, false);
            return ResponseEntity.ok(new ApiResponse<>(true, "Payment cancellation processed successfully", result));
        } catch (Exception e) {
            log.error("Error processing cancel callback: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error processing payment cancellation: " + e.getMessage(), null));
        }
    }

    private String extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetailsWithUserId userDetails) {
            return userDetails.getUserId();
        }
        throw new RuntimeException("Unable to extract user ID from authentication");
    }
}
