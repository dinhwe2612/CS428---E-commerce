package com.catalog.catalog_service.listener;

import com.catalog.catalog_service.RabbitMessage.OrderItemResponseDTO;
import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.event.OutOfStockEvent;
import com.catalog.catalog_service.exception.OutOfStockException;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.model.Inventory;
import com.catalog.catalog_service.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.catalog.catalog_service.RabbitMessage.OrderCreatedMessage;
import com.catalog.catalog_service.producer.RabbitProducer;
import com.catalog.catalog_service.service.InventoryService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ProductListener {
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final RabbitProducer rabbitProducer;

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(OrderCreatedMessage orderCreatedMessage) {
        List<OrderItemResponseDTO> orderItems = orderCreatedMessage.getOrderItems();
        for (OrderItemResponseDTO orderItem : orderItems) {
            Long inventoryId = Long.valueOf(orderItem.getInventoryId());
            Long productId = Long.valueOf(orderItem.getProductId());
            Integer quantity = orderItem.getQuantity();
            // check if product id is valid
            productService.getProductById(productId);
            // update quantity
            try {
                inventoryService.decreaseInventoryQuantity(inventoryId, quantity);
            } catch (OutOfStockException outOfStockException) {
                // emit Out of Stock Event
                rabbitProducer.sendProductOutOfStockMessage(new OutOfStockEvent(
                        orderCreatedMessage.getId(),
                        orderItem.getProductId(),
                        "Product " + productId + " is out of stock."
                ));
                throw outOfStockException;
            }
        }
    }
}
