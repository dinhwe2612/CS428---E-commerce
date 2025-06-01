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

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMessageProducer {

    private final RabbitTemplate rabbitTemplate;

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

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_CREATED_ROUTING_KEY,
                    event
            );

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

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_STATUS_UPDATED_ROUTING_KEY,
                    event
            );

            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.PAYMENT_EXCHANGE,
                        RabbitMQConfig.ORDER_PAYMENT_ROUTING_KEY,
                        event
                );
            }

        } catch (Exception e) {
            log.error("Error sending payment status updated event: {}", e.getMessage(), e);
        }
    }
} 