package com.catalog.catalog_service.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.catalog.catalog_service.dto.DynamicPriceDTO;
import com.catalog.catalog_service.event.ProductPriceUpdateEvent;
import com.catalog.catalog_service.service.DynamicPricingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductPriceListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductPriceListener.class);
    
    private final DynamicPricingService dynamicPricingService;
    
    @EventListener
    public void handleProductPriceUpdateEvent(ProductPriceUpdateEvent event) {
        try {
            logger.debug("Processing price update event for product: {} with reason: {}", 
                event.getProductId(), event.getReason());
            
            DynamicPriceDTO dynamicPrice = dynamicPricingService.calculateDynamicPrice(event.getProductId());
            
            if (dynamicPrice.isPriceChanged()) {
                logger.info("Price updated for product {}: {} -> {} ({}% discount) - Reason: {}", 
                    event.getProductId(),
                    dynamicPrice.getOriginalPrice(),
                    dynamicPrice.getDynamicPrice(),
                    dynamicPrice.getDiscountPercentage(),
                    event.getReason());
            } else {
                logger.debug("No price change for product {}", event.getProductId());
            }
        } catch (Exception e) {
            logger.error("Error processing price update event for product {}: {}", 
                event.getProductId(), e.getMessage(), e);
        }
    }
}
