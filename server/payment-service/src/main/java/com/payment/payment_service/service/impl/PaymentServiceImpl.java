package com.payment.payment_service.service.impl;

import com.payment.integration.order.client.OrderServiceClient;
import com.payment.integration.order.dto.OrderItemResponseDTO;
import com.payment.integration.order.dto.OrderResponseDTO;
import com.payment.integration.order.dto.GuestOrderResponseDTO;
import com.payment.payment_service.dto.PaymentRequestDTO;
import com.payment.payment_service.dto.PaymentResponseDTO;
import com.payment.payment_service.dto.GuestPaymentRequestDTO;
import com.payment.payment_service.exception.PaymentNotFoundException;
import com.payment.payment_service.exception.PaymentProcessingException;
import com.payment.payment_service.model.Payment;
import com.payment.payment_service.model.PaymentMethod;
import com.payment.payment_service.model.PaymentStatus;
import com.payment.payment_service.repository.PaymentRepository;
import com.payment.payment_service.service.PaymentService;
import com.payment.payment_service.service.PaymentMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayOS payOS;
    private final PaymentMessageProducer messageProducer;
    private final OrderServiceClient orderServiceClient;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequest, String userId) {
        try {
            OrderResponseDTO order = orderServiceClient.getOrderById(paymentRequest.getOrderId());
            System.out.println("Order: " + order);

            if (!order.getStatus().equals("PENDING")) {
                throw new PaymentProcessingException("Order is not in pending status");
            }
            
            if (!order.getPaymentMethod().equals("CARD")) {
                throw new PaymentProcessingException("Order is not using card payment");
            }

            Payment payment = createPaymentEntity(order, userId);
            Payment savedPayment = paymentRepository.save(payment);

            PaymentResponseDTO response = convertToDTO(savedPayment);
            
            response = createPayOSPayment(savedPayment, paymentRequest, order);

            messageProducer.sendPaymentCreatedEvent(response);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error creating payment: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to create payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO createGuestPayment(GuestPaymentRequestDTO paymentRequest) {
        try {
            GuestOrderResponseDTO order = orderServiceClient.getGuestOrderById(paymentRequest.getOrderId());
            System.out.println("Guest Order: " + order);

            if (!order.getStatus().equals("PENDING")) {
                throw new PaymentProcessingException("Guest order is not in pending status");
            }
            
            if (!order.getPaymentMethod().equals("CARD")) {
                // print the payment method
                System.out.println("Guest order is not using card payment: " + order.getPaymentMethod());
                throw new PaymentProcessingException("Guest order is not using card payment");
            }

            // Create payment entity for guest user (no userId)
            Payment payment = createGuestPaymentEntity(order, paymentRequest);
            Payment savedPayment = paymentRepository.save(payment);

            PaymentResponseDTO response = convertToDTO(savedPayment);
            
            response = createGuestPayOSPayment(savedPayment, paymentRequest, order);

            messageProducer.sendPaymentCreatedEvent(response);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error creating guest payment: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to create guest payment: " + e.getMessage());
        }
    }

    private Payment createPaymentEntity(OrderResponseDTO order, String userId) {
        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setOrderId(order.getId());
        payment.setUserId(userId);
        payment.setAmount(BigDecimal.valueOf(order.getTotalAmount()));
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCurrency("VND");
        payment.setDescription("Payment for order #" + order.getId());
        payment.setPaymentGateway("PayOS");
        return payment;
    }

    private Payment createGuestPaymentEntity(GuestOrderResponseDTO order, GuestPaymentRequestDTO paymentRequest) {
        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setOrderId(order.getId());
        payment.setUserId("GUEST_" + order.getCustomerEmail()); // Use email as guest identifier
        payment.setAmount(BigDecimal.valueOf(order.getTotalAmount()));
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCurrency("VND");
        payment.setDescription("Guest payment for order #" + order.getId());
        payment.setPaymentGateway("PayOS");
        return payment;
    }

    private PaymentResponseDTO createPayOSPayment(Payment payment, PaymentRequestDTO request, OrderResponseDTO order) {
        try {
            log.info("Creating PayOS payment for order: {}", payment.getOrderId());
            
            ItemData itemData = ItemData.builder()
                .name("Order #" + payment.getOrderId())
                .quantity(1)
                .price(payment.getAmount().intValue())
                .build();

            PaymentData.PaymentDataBuilder paymentDataBuilder = PaymentData.builder()
                .orderCode(payment.getOrderId())
                .amount(payment.getAmount().intValue())
                .description("Order #" + payment.getOrderId())
                .item(itemData);
            
            if (request.getReturnUrl() != null) {
                paymentDataBuilder.returnUrl(request.getReturnUrl());
            }
            if (request.getCancelUrl() != null) {
                paymentDataBuilder.cancelUrl(request.getCancelUrl());
            }
            
            PaymentData paymentData = paymentDataBuilder.build();

            CheckoutResponseData createPaymentResult = payOS.createPaymentLink(paymentData);
            
            payment.setGatewayTransactionId(payment.getOrderId().toString());
            payment.setStatus(PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);

            PaymentResponseDTO response = convertToDTO(payment);
            response.setPaymentUrl(createPaymentResult.getCheckoutUrl());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error creating PayOS payment: {}", e.getMessage(), e);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            
            throw new PaymentProcessingException("Failed to create PayOS payment: " + e.getMessage());
        }
    }

    private PaymentResponseDTO createGuestPayOSPayment(Payment payment, GuestPaymentRequestDTO request, GuestOrderResponseDTO order) {
        try {
            log.info("Creating guest PayOS payment for order: {}", payment.getOrderId());
            
            ItemData itemData = ItemData.builder()
                .name("Order #" + payment.getOrderId())
                .quantity(1)
                .price(payment.getAmount().intValue())
                .build();

            PaymentData.PaymentDataBuilder paymentDataBuilder = PaymentData.builder()
                .orderCode(payment.getOrderId())
                .amount(payment.getAmount().intValue())
                .description("Guest payment for order #" + payment.getOrderId())
                .item(itemData);
            
            if (request.getReturnUrl() != null) {
                paymentDataBuilder.returnUrl(request.getReturnUrl());
            }
            if (request.getCancelUrl() != null) {
                paymentDataBuilder.cancelUrl(request.getCancelUrl());
            }
            
            PaymentData paymentData = paymentDataBuilder.build();

            CheckoutResponseData createPaymentResult = payOS.createPaymentLink(paymentData);
            
            payment.setGatewayTransactionId(payment.getOrderId().toString());
            payment.setStatus(PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);

            PaymentResponseDTO response = convertToDTO(payment);
            response.setPaymentUrl(createPaymentResult.getCheckoutUrl());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error creating guest PayOS payment: {}", e.getMessage(), e);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            
            throw new PaymentProcessingException("Failed to create guest PayOS payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
        return convertToDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with transaction id: " + transactionId));
        return convertToDTO(payment);
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PaymentResponseDTO> getGuestPaymentsByEmail(String email) {
        String guestUserId = "GUEST_" + email;
        return paymentRepository.findByUserId(guestUserId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(String transactionId, PaymentStatus status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with transaction id: " + transactionId));
        
        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(status);
        payment = paymentRepository.save(payment);

        PaymentResponseDTO response = convertToDTO(payment);
        
        messageProducer.sendPaymentStatusUpdatedEvent(response, oldStatus);
        
        return response;
    }

    @Override
    @Transactional
    public void processWebhook(Map<String, Object> webhook) {
        try {
            log.info("Processing PayOS webhook: {}", webhook);
            
            Object orderCodeObj = webhook.get("orderCode");
            if (orderCodeObj == null) {
                Object dataObj = webhook.get("data");
                if (dataObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    orderCodeObj = data.get("orderCode");
                }
            }
            
            if (orderCodeObj == null) {
                log.warn("No orderCode found in webhook data");
                return;
            }
            
            Long orderCode = ((Number) orderCodeObj).longValue();
            log.info("Processing webhook for order: {}", orderCode);

            Payment payment = paymentRepository.findByGatewayTransactionId(orderCode.toString())
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order code: " + orderCode));

            PaymentStatus oldStatus = payment.getStatus();
            
            String code = (String) webhook.getOrDefault("code", "unknown");
            if (code.equals("unknown")) {
                Object dataObj = webhook.get("data");
                if (dataObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    code = (String) data.getOrDefault("code", "unknown");
                }
            }
            
            if ("00".equals(code)) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setCompletedAt(LocalDateTime.now());
                log.info("Payment completed for order: {}", orderCode);
            } else if ("01".equals(code)) {
                payment.setStatus(PaymentStatus.CANCELLED);
                log.info("Payment cancelled for order: {}", orderCode);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                log.warn("Payment failed for order: {} with code: {}", orderCode, code);
            }
            
            String desc = (String) webhook.getOrDefault("desc", "");
            if (desc.isEmpty()) {
                Object dataObj = webhook.get("data");
                if (dataObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    desc = (String) data.getOrDefault("desc", "");
                }
            }
            
            payment.setGatewayResponse(desc);
            payment = paymentRepository.save(payment);

            PaymentResponseDTO response = convertToDTO(payment);
            messageProducer.sendPaymentStatusUpdatedEvent(response, oldStatus);
            
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to process webhook: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO cancelPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with transaction id: " + transactionId));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new PaymentProcessingException("Cannot cancel completed payment");
        }
        
        try {
            if (payment.getGatewayTransactionId() != null && 
                (payment.getStatus() == PaymentStatus.PROCESSING || payment.getStatus() == PaymentStatus.PENDING)) {
                
                Long orderCode = Long.parseLong(payment.getGatewayTransactionId());
                PaymentLinkData cancelResult = payOS.cancelPaymentLink(orderCode, "User requested cancellation");
                log.info("Payment cancelled in PayOS: {}", cancelResult);
            }
            
            PaymentStatus oldStatus = payment.getStatus();
            payment.setStatus(PaymentStatus.CANCELLED);
            payment = paymentRepository.save(payment);

            PaymentResponseDTO response = convertToDTO(payment);
            messageProducer.sendPaymentStatusUpdatedEvent(response, oldStatus);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error cancelling payment: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to cancel payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(String transactionId, String reason) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with transaction id: " + transactionId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentProcessingException("Can only refund completed payments");
        }
        
        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setGatewayResponse(reason);
        payment = paymentRepository.save(payment);

        PaymentResponseDTO response = convertToDTO(payment);
        messageProducer.sendPaymentStatusUpdatedEvent(response, oldStatus);
        
        log.info("Payment marked as refunded: {} - Reason: {}", transactionId, reason);
        return response;
    }

    private PaymentResponseDTO convertToDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setOrderId(payment.getOrderId());
        dto.setUserId(payment.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setCurrency(payment.getCurrency());
        dto.setDescription(payment.getDescription());
        dto.setPaymentGateway(payment.getPaymentGateway());
        dto.setGatewayTransactionId(payment.getGatewayTransactionId());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        dto.setCompletedAt(payment.getCompletedAt());
        return dto;
    }

    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    @Transactional
    public String processReturnUrl(String code, String id, Long orderCode, String status, boolean isSuccess) {
        try {
            log.info("Processing return URL - code: {}, id: {}, orderCode: {}, status: {}, isSuccess: {}", 
                    code, id, orderCode, status, isSuccess);

            Payment payment = paymentRepository.findByGatewayTransactionId(orderCode.toString())
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order code: " + orderCode));

            PaymentStatus currentStatus = payment.getStatus();
            
            if (currentStatus == PaymentStatus.COMPLETED) {
                log.info("Payment already completed for order: {}", orderCode);
                return "Payment already completed";
            }
            
            if (currentStatus == PaymentStatus.CANCELLED || currentStatus == PaymentStatus.FAILED) {
                log.info("Payment already finalized with status {} for order: {}", currentStatus, orderCode);
                return "Payment already " + currentStatus.toString().toLowerCase();
            }

            if (currentStatus == PaymentStatus.PENDING) {
                log.warn("Possible attempt to manipulate pending payment for order: {}. Current status: {}", 
                        orderCode, currentStatus);
                throw new PaymentProcessingException("Cannot process return URL for pending payment. Use proper payment flow.");
            }

            try {
                log.info("Verifying payment status with PayOS for order: {}", orderCode);
                vn.payos.type.PaymentLinkData payosPayment = payOS.getPaymentLinkInformation(orderCode);
                
                if (payosPayment == null) {
                    log.error("PayOS payment not found for order: {}", orderCode);
                    throw new PaymentProcessingException("Payment not found in PayOS system");
                }
                
                String payosStatus = payosPayment.getStatus();
                log.info("PayOS status for order {}: {}", orderCode, payosStatus);
                
                if (isSuccess && "00".equals(code)) {
                    if (!"PAID".equals(payosStatus)) {
                        log.warn("Attempted success fraud detected. PayOS status: {}, Order: {}", payosStatus, orderCode);
                        throw new PaymentProcessingException("Payment not actually completed in PayOS. Status: " + payosStatus);
                    }
                } else {
                    if ("PAID".equals(payosStatus)) {
                        log.warn("Attempted cancel fraud detected. PayOS shows payment completed for order: {}", orderCode);
                        payment.setStatus(PaymentStatus.COMPLETED);
                        payment.setCompletedAt(LocalDateTime.now());
                        payment.setGatewayResponse("Auto-corrected: PayOS shows PAID status");
                        payment = paymentRepository.save(payment);
                        
                        PaymentResponseDTO response = convertToDTO(payment);
                        messageProducer.sendPaymentStatusUpdatedEvent(response, currentStatus);
                        
                        return "Payment actually completed (corrected from PayOS)";
                    }
                }
                
            } catch (Exception payosException) {
                log.error("Error verifying with PayOS for order {}: {}", orderCode, payosException.getMessage());
                throw new PaymentProcessingException("Failed to verify payment status with PayOS: " + payosException.getMessage());
            }

            PaymentStatus newStatus;
            PaymentStatus oldStatus = payment.getStatus();
            
            if (isSuccess && "00".equals(code)) {
                if (currentStatus != PaymentStatus.PROCESSING) {
                    log.warn("Invalid status transition for success. Current: {}, Order: {}", currentStatus, orderCode);
                    throw new PaymentProcessingException("Invalid payment status for success callback");
                }
                newStatus = PaymentStatus.COMPLETED;
                payment.setCompletedAt(LocalDateTime.now());
                log.info("Payment marked as completed for order: {}", orderCode);
            } else {
                if (currentStatus != PaymentStatus.PROCESSING) {
                    log.warn("Invalid status transition for cancel. Current: {}, Order: {}", currentStatus, orderCode);
                    throw new PaymentProcessingException("Invalid payment status for cancel callback");
                }
                newStatus = "01".equals(code) ? PaymentStatus.CANCELLED : PaymentStatus.FAILED;
                log.info("Payment marked as {} for order: {}", newStatus, orderCode);
            }
            
            payment.setStatus(newStatus);
            payment.setGatewayResponse("Return URL processed with code: " + code + " (PayOS verified)");
            payment = paymentRepository.save(payment);

            PaymentResponseDTO response = convertToDTO(payment);
            messageProducer.sendPaymentStatusUpdatedEvent(response, oldStatus);
            
            return "Payment " + newStatus.toString().toLowerCase();
            
        } catch (Exception e) {
            log.error("Error processing return URL: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to process return URL: " + e.getMessage());
        }
    }
} 