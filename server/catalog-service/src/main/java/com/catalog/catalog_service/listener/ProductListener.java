package com.catalog.catalog_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.catalog.catalog_service.RabbitMessage.OrderCreatedMessage;
import com.catalog.catalog_service.RabbitMessage.OrderItemResponseDTO;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.producer.RabbitProducer;
import com.catalog.catalog_service.service.InventoryService;
import com.catalog.catalog_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductListener {
    private final InventoryService inventoryService;
    private final ProductService productService;
    private final RabbitProducer rabbitProducer;

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(OrderCreatedMessage orderCreatedMessage) {
        log.info("Received order created event: {}", orderCreatedMessage);

        for (OrderItemResponseDTO orderItem : orderCreatedMessage.getOrderItems()) {
            try {
                // Create inventory update request
                UpdateInventoryRequest updateRequest = new UpdateInventoryRequest();
                
                // Get current inventory state
                Long productId = Long.parseLong(orderItem.getProductId());
                var currentInventory = inventoryService.getInventoryByProductId(productId);
                
                // Calculate new values
                int newAvailableStock = currentInventory.getAvailableStock() - orderItem.getQuantity();
                int newReservedQuantity = currentInventory.getReservedQuantity() + orderItem.getQuantity();
                if(newAvailableStock < 0){
                   
                    rabbitProducer.sendProductOutOfStockMessage(orderItem.getProductId());
                  
                    return;
                }

                // Set update values
                updateRequest.setAvailableStock(newAvailableStock);
                updateRequest.setReservedQuantity(newReservedQuantity);
                updateRequest.setCurrentStock(currentInventory.getCurrentStock());
                updateRequest.setReorderLevel(currentInventory.getReorderLevel());
                updateRequest.setReorderQuantity(currentInventory.getReorderQuantity());
                updateRequest.setLowStockThreshold(currentInventory.getLowStockThreshold());
                updateRequest.setUnitCost(currentInventory.getUnitCost());
                updateRequest.setLocation(currentInventory.getLocation());
                updateRequest.setStatus(currentInventory.getStatus());
                updateRequest.setSupplierId(currentInventory.getSupplierId());

                // Update inventory
                inventoryService.updateInventory(productId, updateRequest);
                
                log.info("Successfully updated inventory for product {} with quantity {}", 
                    productId, orderItem.getQuantity());
            } catch (Exception e) {
                log.error("Error processing inventory update for product {}: {}", 
                    orderItem.getProductId(), e.getMessage());
                
            }
        }
    }
}
