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
    //test 4
    private final OrderService orderService;

    @RabbitListener(queues = "order.payment.queue")
    public void handlePaymentStatusUpdate(PaymentStatusUpdatedEvent event) {
        log.info("=== PAYMENT EVENT RECEIVED ===");
        log.info("Event details: {}", event);
        log.info("Order ID: {}", event.getOrderId());
        log.info("Transaction ID: {}", event.getTransactionId());
        log.info("Payment Status: {} -> {}", event.getOldStatus(), event.getNewStatus());
        
        try {
            if (event.getOrderId() == null) {
                log.error("Order ID is null in payment event: {}", event);
                return;
            }

            if (event.getNewStatus() == null) {
                log.error("Payment status is null in payment event: {}", event);
                return;
            }

            log.info("Processing payment status update for order: {}, payment: {}, status: {} -> {}", 
                event.getOrderId(), event.getTransactionId(), event.getOldStatus(), event.getNewStatus());
            
            String orderStatus = mapPaymentStatusToOrderStatus(event.getNewStatus());
            log.info("Mapped payment status '{}' to order status '{}'", event.getNewStatus(), orderStatus);
            
            if (orderStatus != null) {
                log.info("Calling orderService.updateOrderStatus({}, {})", event.getOrderId(), orderStatus);
                orderService.updateOrderStatus(event.getOrderId(), orderStatus);
                log.info("Successfully updated order {} status to: {}", event.getOrderId(), orderStatus);
            } else {
                log.warn("No order status mapping found for payment status: {}", event.getNewStatus());
            }
            
        } catch (Exception e) {
            log.error("Error processing payment status updated event for order {}: {}", 
                event.getOrderId(), e.getMessage(), e);
            log.error("Full exception details: ", e);
        }
        
        log.info("=== PAYMENT EVENT PROCESSING COMPLETED ===");
    }

    private String mapPaymentStatusToOrderStatus(String paymentStatus) {
        log.debug("Mapping payment status: {}", paymentStatus);
        
        if (paymentStatus == null) {
            log.warn("Payment status is null");
            return null;
        }
        
        String result;
        switch (paymentStatus.toUpperCase()) {
            case "PENDING":
                result = "PAYMENT_PENDING";
                break;
            case "PROCESSING":
                result = "PAYMENT_PROCESSING";
                break;
            case "COMPLETED":
                result = "PAID";
                break;
            case "FAILED":
                result = "PAYMENT_FAILED";
                break;
            case "CANCELLED":
                result = "PAYMENT_CANCELLED";
                break;
            case "REFUNDED":
                result = "REFUNDED";
                break;
            default:
                log.warn("Unknown payment status: {}", paymentStatus);
                result = null;
        }
        
        log.debug("Payment status '{}' mapped to order status '{}'", paymentStatus, result);
        return result;
    }

} 