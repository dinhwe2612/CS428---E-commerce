package com.catalog.catalog_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.catalog.catalog_service.RabbitMessage.OrderCreatedMessage;
import com.catalog.catalog_service.RabbitMessage.OrderItemResponseDTO;
import com.catalog.catalog_service.dto.request.UpdateInventoryRequest;
import com.catalog.catalog_service.event.OutOfStockEvent;
import com.catalog.catalog_service.producer.RabbitProducer;
import com.catalog.catalog_service.service.InventoryService;
import com.catalog.catalog_service.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
@AllArgsConstructor
public class ProductListener {
    private  InventoryService inventoryService;
    private  RabbitProducer rabbitProducer;

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(OrderCreatedMessage orderCreatedMessage) {
        
        //print the orderCreatedMessage.orderItems IT IS A LIST OF ORDER ITEMS
        System.out.println("orderCreatedMessage.orderItems: " + orderCreatedMessage.getOrderItems());

        for (OrderItemResponseDTO orderItem : orderCreatedMessage.getOrderItems()) {
            try {
                // Create inventory update request
                UpdateInventoryRequest updateRequest = new UpdateInventoryRequest();
                
                // Get current inventory state
                Long inventoryId = Long.parseLong(orderItem.getInventoryId());
                var currentInventory = inventoryService.getInventoryById(inventoryId);
                //print the currentInventory
                System.out.println("currentInventory: " + currentInventory);
                // Calculate new values
                int newAvailableStock = currentInventory.getAvailableStock() - orderItem.getQuantity();
                //print the newAvailableStock
                System.out.println("newAvailableStock: " + newAvailableStock);
                int newReservedQuantity = currentInventory.getReservedQuantity() + orderItem.getQuantity();
                if(newAvailableStock < 0){
                   
                    rabbitProducer.sendProductOutOfStockMessage(
                        new OutOfStockEvent(
                            orderCreatedMessage.getId(),
                            orderItem.getProductId(),
                            "Product out of stock"
                        )

                    );
                 System.out.println("product out of stock");
                    return;
                }

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
                inventoryService.updateInventory(currentInventory.getId(), updateRequest);
             
            } catch (Exception e) {

            }
        }
    }
}
