package com.order.order_service.listener;

import com.order.order_service.event.PaymentStatusUpdatedEvent;
import com.order.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentListener {

    private final OrderService orderService;

    @RabbitListener(queues = "order.payment.queue")
    public void handlePaymentStatusUpdate(PaymentStatusUpdatedEvent event) {
        try {
            log.info("Received payment status updated event for order: {}, payment: {}, status: {} -> {}", 
                event.getOrderId(), event.getTransactionId(), event.getOldStatus(), event.getNewStatus());
            
            // Map payment status to order status
            String orderStatus = mapPaymentStatusToOrderStatus(event.getNewStatus());
            
            if (orderStatus != null) {
                orderService.updateOrderStatus(event.getOrderId(), orderStatus);
                log.info("Updated order {} status to: {}", event.getOrderId(), orderStatus);
            }
            
        } catch (Exception e) {
            log.error("Error processing payment status updated event for order {}: {}", 
                event.getOrderId(), e.getMessage(), e);
        }
    }

    private String mapPaymentStatusToOrderStatus(String paymentStatus) {
        switch (paymentStatus) {
            case "PENDING":
                return "PAYMENT_PENDING";
            case "PROCESSING":
                return "PAYMENT_PROCESSING";
            case "COMPLETED":
                return "PAID";
            case "FAILED":
                return "PAYMENT_FAILED";
            case "CANCELLED":
                return "PAYMENT_CANCELLED";
            case "REFUNDED":
                return "REFUNDED";
            default:
                log.warn("Unknown payment status: {}", paymentStatus);
                return null;
        }
    }

} 