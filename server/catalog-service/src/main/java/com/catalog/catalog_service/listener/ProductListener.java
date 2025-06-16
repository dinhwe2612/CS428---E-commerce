package com.catalog.catalog_service.listener;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.catalog.catalog_service.RabbitMessage.OrderCreatedMessage;
import com.catalog.catalog_service.RabbitMessage.OrderItemResponseDTO;
import com.catalog.catalog_service.event.OutOfStockEvent;
import com.catalog.catalog_service.exception.OutOfStockException;
import com.catalog.catalog_service.producer.RabbitProducer;
import com.catalog.catalog_service.service.InventoryService;
import com.catalog.catalog_service.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ProductListener {
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final RabbitProducer rabbitProducer;

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(OrderCreatedMessage orderCreatedMessage)throws InterruptedException {
        List<OrderItemResponseDTO> orderItems = orderCreatedMessage.getOrderItems();
        for (OrderItemResponseDTO orderItem : orderItems) {
            Long inventoryId = Long.valueOf(orderItem.getInventoryId());
            Long productId = Long.valueOf(orderItem.getProductId());
            Integer quantity = orderItem.getQuantity();
            
            productService.getProductById(productId);
           
            try {
                inventoryService.decreaseInventoryQuantity(inventoryId, quantity);
            } catch (OutOfStockException outOfStockException) {
                
                rabbitProducer.sendProductOutOfStockMessage(new OutOfStockEvent(
                        orderCreatedMessage.getId(),
                        orderItem.getProductId(),
                        "Product " + productId + " is out of stock."
                ));
                throw outOfStockException;
            }
            catch (ObjectOptimisticLockingFailureException lockEx) {
                log.warn("Optimistic lock failure on product {}, will retry up to 3 times", productId);
            
                
                for (int attempt = 1; attempt <= 3; attempt++) {
                    try {
                        
                        long backoffMillis = (long) Math.pow(2, attempt) * 1000L;
                        Thread.sleep(backoffMillis);
            
                        
                        inventoryService.decreaseInventoryQuantity(inventoryId, quantity);
            
                        
                        return;
                    } catch (ObjectOptimisticLockingFailureException retryLockEx) {
                        log.warn("Retry {}/3 failed for product {} due to optimistic locking", attempt, productId, retryLockEx);
                       
                    }
                }
            
                
                throw new OutOfStockException("Product " + productId + " is out of stock after concurrent updates.");
            }
            
        }
    }
}


