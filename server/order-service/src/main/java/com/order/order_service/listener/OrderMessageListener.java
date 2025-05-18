package com.order.order_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.order.order_service.event.OutOfStockEvent;
import com.order.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageListener {

    private final OrderService orderService;

    @RabbitListener(queues = "product.outofstock.queue")
    public void handleOutOfStockEvent(OutOfStockEvent event) {
        log.info("Received out-of-stock event: {}", event.getOrderId());
        orderService.updateOrderStatus(event.getOrderId(), "CANCELLED");
    }
} 