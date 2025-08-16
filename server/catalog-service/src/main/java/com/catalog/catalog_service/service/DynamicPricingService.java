package com.catalog.catalog_service.service;

import java.math.BigDecimal;
import java.util.List;

import com.catalog.catalog_service.dto.DynamicPriceDTO;
import com.catalog.catalog_service.model.Product;

public interface DynamicPricingService {
    
    DynamicPriceDTO calculateDynamicPrice(Product product);
    
    DynamicPriceDTO calculateDynamicPrice(Long productId);
    
    List<DynamicPriceDTO> calculateDynamicPricesForProducts(List<Long> productIds);
    
    BigDecimal getEffectivePrice(Product product);
    
    String checkSpecialDay();
}
