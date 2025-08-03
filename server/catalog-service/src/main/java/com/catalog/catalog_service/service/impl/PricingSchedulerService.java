package com.catalog.catalog_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.catalog.catalog_service.service.DynamicPricingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(PricingSchedulerService.class);
    
    private final DynamicPricingService dynamicPricingService;
    
    @Scheduled(fixedRate = 300000)
    public void updatePricesBasedOnTimeOfDay() {
        try {
            logger.info("Starting scheduled price update based on time of day");
            dynamicPricingService.updateAllProductPrices();
            logger.info("Completed scheduled price update");
        } catch (Exception e) {
            logger.error("Error during scheduled price update: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void updatePricesBasedOnSpecialDays() {
        try {
            logger.info("Starting daily price update for special days");
            String specialDay = dynamicPricingService.checkSpecialDay();
            if (specialDay != null) {
                logger.info("Special day detected: {}. Updating all product prices.", specialDay);
                dynamicPricingService.updateAllProductPrices();
            } else {
                logger.info("No special day detected. Regular pricing applies.");
            }
        } catch (Exception e) {
            logger.error("Error during special day price update: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(cron = "0 0 1 * * ?")
    public void updatePricesBasedOnInventoryAge() {
        try {
            logger.info("Starting daily price update based on inventory age");
            dynamicPricingService.updateAllProductPrices();
            logger.info("Completed inventory age-based price update");
        } catch (Exception e) {
            logger.error("Error during inventory age price update: {}", e.getMessage(), e);
        }
    }
}
