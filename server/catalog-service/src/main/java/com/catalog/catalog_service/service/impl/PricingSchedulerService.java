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
    
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void logSpecialDayStatus() {
        try {
            logger.info("Checking special day status");
            String specialDay = dynamicPricingService.checkSpecialDay();
            if (specialDay != null) {
                logger.info("Special day detected: {}. Dynamic pricing rules will be applied on-demand.", specialDay);
            } else {
                logger.info("No special day detected. Regular pricing rules will be applied on-demand.");
            }
        } catch (Exception e) {
            logger.error("Error during special day check: {}", e.getMessage(), e);
        }
    }
}
