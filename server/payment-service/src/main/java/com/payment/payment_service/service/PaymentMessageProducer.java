package com.payment.payment_service.service;

import com.payment.payment_service.config.RabbitMQConfig;
import com.payment.payment_service.dto.PaymentResponseDTO;
import com.payment.payment_service.event.PaymentCreatedEvent;
import com.payment.payment_service.event.PaymentStatusUpdatedEvent;
import com.payment.payment_service.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public static class OrderPaymentEvent {
        private String transactionId;
        private Long orderId;
        private String userId;
        private String oldStatus;
        private String newStatus;
        private LocalDateTime completedAt;

        public OrderPaymentEvent() {}

        public OrderPaymentEvent(String transactionId, Long orderId, String userId, 
                               String oldStatus, String newStatus, LocalDateTime completedAt) {
            this.transactionId = transactionId;
            this.orderId = orderId;
            this.userId = userId;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
            this.completedAt = completedAt;
        }

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getOldStatus() { return oldStatus; }
        public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

        public String getNewStatus() { return newStatus; }
        public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    }

    public void sendPaymentCreatedEvent(PaymentResponseDTO payment) {
        try {
            PaymentCreatedEvent event = new PaymentCreatedEvent(
                    payment.getTransactionId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getAmount(),
                    payment.getPaymentMethod(),
                    payment.getStatus(),
                    payment.getPaymentUrl()
            );

            log.info("Sending payment created event for order: {}, transaction: {}", 
                    payment.getOrderId(), payment.getTransactionId());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_CREATED_ROUTING_KEY,
                    event
            );

            log.info("Payment created event sent successfully");

        } catch (Exception e) {
            log.error("Error sending payment created event: {}", e.getMessage(), e);
        }
    }

    public void sendPaymentStatusUpdatedEvent(PaymentResponseDTO payment, PaymentStatus oldStatus) {
        try {
            PaymentStatusUpdatedEvent event = new PaymentStatusUpdatedEvent(
                    payment.getTransactionId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    oldStatus,
                    payment.getStatus(),
                    payment.getCompletedAt()
            );

            log.info("Sending payment status updated event for order: {}, transaction: {}, status: {} -> {}", 
                    payment.getOrderId(), payment.getTransactionId(), oldStatus, payment.getStatus());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_STATUS_UPDATED_ROUTING_KEY,
                    event
            );

            log.info("Payment status updated event sent successfully");

            if (payment.getStatus() == PaymentStatus.COMPLETED || 
                payment.getStatus() == PaymentStatus.CANCELLED || 
                payment.getStatus() == PaymentStatus.FAILED ||
                payment.getStatus() == PaymentStatus.REFUNDED) {
                
                log.info("=== SENDING ORDER PAYMENT NOTIFICATION ===");
                log.info("Order ID: {}", payment.getOrderId());
                log.info("Payment Status: {} -> {}", oldStatus, payment.getStatus());
                log.info("Exchange: {}", RabbitMQConfig.PAYMENT_EXCHANGE);
                log.info("Routing Key: {}", RabbitMQConfig.ORDER_PAYMENT_ROUTING_KEY);
                
                OrderPaymentEvent orderEvent = new OrderPaymentEvent(
                        payment.getTransactionId(),
                        payment.getOrderId(),
                        payment.getUserId(),
                        oldStatus.toString(),
                        payment.getStatus().toString(),
                        payment.getCompletedAt()
                );

                log.info("Created OrderPaymentEvent: transactionId={}, orderId={}, oldStatus={}, newStatus={}", 
                        orderEvent.getTransactionId(), orderEvent.getOrderId(), 
                        orderEvent.getOldStatus(), orderEvent.getNewStatus());

                try {
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.PAYMENT_EXCHANGE,
                            RabbitMQConfig.ORDER_PAYMENT_ROUTING_KEY,
                            orderEvent
                    );
                    log.info("Order payment notification sent successfully for order: {} with status: {}", 
                            payment.getOrderId(), payment.getStatus());
                } catch (Exception sendException) {
                    log.error("Failed to send order payment notification: {}", sendException.getMessage(), sendException);
                }
                
                log.info("=== ORDER PAYMENT NOTIFICATION COMPLETED ===");
            }

        } catch (Exception e) {
            log.error("Error sending payment status updated event: {}", e.getMessage(), e);
        }
    }
} 