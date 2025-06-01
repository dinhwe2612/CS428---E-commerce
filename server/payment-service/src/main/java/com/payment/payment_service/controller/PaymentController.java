package com.payment.payment_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.payment.payment_service.dto.ApiResponse;
import com.payment.payment_service.dto.PaymentRequestDTO;
import com.payment.payment_service.dto.PaymentResponseDTO;
import com.payment.payment_service.model.PaymentStatus;
import com.payment.payment_service.security.UserDetailsWithUserId;
import com.payment.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> createPayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequest,
            Authentication authentication) {
        
        String userId = extractUserIdFromAuthentication(authentication);
        PaymentResponseDTO response = paymentService.createPayment(paymentRequest, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved successfully", response));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentByTransactionId(
            @PathVariable String transactionId) {
        PaymentResponseDTO response = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved successfully", response));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPaymentsByOrderId(
            @PathVariable Long orderId) {
        List<PaymentResponseDTO> responses = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", responses));
    }

    @GetMapping("/user/my-payments")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getMyPayments(
            Authentication authentication) {
        String userId = extractUserIdFromAuthentication(authentication);
        List<PaymentResponseDTO> responses = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", responses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status) {
        List<PaymentResponseDTO> responses = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", responses));
    }

    @PutMapping("/transaction/{transactionId}/status")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> updatePaymentStatus(
            @PathVariable String transactionId,
            @RequestParam PaymentStatus status) {
        PaymentResponseDTO response = paymentService.updatePaymentStatus(transactionId, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment status updated successfully", response));
    }

    @PostMapping("/transaction/{transactionId}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> cancelPayment(
            @PathVariable String transactionId) {
        PaymentResponseDTO response = paymentService.cancelPayment(transactionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment cancelled successfully", response));
    }

    @PostMapping("/transaction/{transactionId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> refundPayment(
            @PathVariable String transactionId,
            @RequestParam(required = false, defaultValue = "User requested refund") String reason) {
        PaymentResponseDTO response = paymentService.refundPayment(transactionId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment refunded successfully", response));
    }

    @PostMapping("/webhook/payos")
    public ResponseEntity<ObjectNode> handlePayOSWebhook(@RequestBody Map<String, Object> body) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        
        try {
            log.info("Received PayOS webhook: {}", body);
            
            paymentService.processWebhook(body);
            
            response.put("error", 0);
            response.put("message", "Webhook processed successfully");
            response.set("data", null);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing PayOS webhook: {}", e.getMessage(), e);
            
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetailsWithUserId userDetails) {
            return userDetails.getUserId();
        }
        throw new RuntimeException("Unable to extract user ID from authentication");
    }
} 