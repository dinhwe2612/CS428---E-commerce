package com.payment.payment_service.service;

import com.payment.payment_service.dto.PaymentRequestDTO;
import com.payment.payment_service.dto.PaymentResponseDTO;
import com.payment.payment_service.dto.GuestPaymentRequestDTO;
import com.payment.payment_service.model.PaymentStatus;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    
    PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequest, String userId);
    
    PaymentResponseDTO createGuestPayment(GuestPaymentRequestDTO paymentRequest);
    
    PaymentResponseDTO getPaymentById(Long id);
    
    PaymentResponseDTO getPaymentByTransactionId(String transactionId);
    
    List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId);
    
    List<PaymentResponseDTO> getPaymentsByUserId(String userId);
    
    List<PaymentResponseDTO> getGuestPaymentsByEmail(String email);
    
    List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status);
    
    PaymentResponseDTO updatePaymentStatus(String transactionId, PaymentStatus status);
    
    void processWebhook(Map<String, Object> webhook);
    
    PaymentResponseDTO cancelPayment(String transactionId);
    
    PaymentResponseDTO refundPayment(String transactionId, String reason);
    
    String processReturnUrl(String code, String id, Long orderCode, String status, boolean isSuccess);
} 