package com.catalog.catalog_service.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catalog.catalog_service.dto.DynamicPriceDTO;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.model.PricingRule;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.model.ProductStatus;
import com.catalog.catalog_service.repository.jpa.PricingRuleRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;
import com.catalog.catalog_service.service.DynamicPricingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DynamicPricingServiceImpl implements DynamicPricingService {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicPricingServiceImpl.class);
    private static final ZoneId VIETNAM_TIMEZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    
    private final ProductRepository productRepository;
    private final PricingRuleRepository pricingRuleRepository;
    
    @Override
    public DynamicPriceDTO calculateDynamicPrice(Product product) {
        try {
            BigDecimal originalPrice = new BigDecimal(product.getPrice().replace(",", ""));
            List<PricingRule> applicableRules = getApplicableRules(product);

            logger.debug("List applicable rules for product {}: {}", 
                product.getId(), applicableRules.stream().map(PricingRule::getRuleName).collect(Collectors.joining(", ")));
            
            BigDecimal finalPrice = originalPrice;
            List<String> appliedRules = new ArrayList<>();
            
            for (PricingRule rule : applicableRules) {
                if (isRuleApplicable(rule, product)) {
                    BigDecimal rulePrice = applyPricingRule(finalPrice, rule);
                    
                    if (rule.getMinPrice() != null && rulePrice.compareTo(rule.getMinPrice()) < 0) {
                        rulePrice = rule.getMinPrice();
                    }
                    
                    if (!rulePrice.equals(finalPrice)) {
                        finalPrice = rulePrice;
                        appliedRules.add(rule.getRuleName());
                    }
                }
            }
            
            BigDecimal discountAmount = originalPrice.subtract(finalPrice);
            Double discountPercentage = originalPrice.compareTo(BigDecimal.ZERO) > 0 ? 
                discountAmount.divide(originalPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;
            
            return new DynamicPriceDTO(
                product.getId(),
                originalPrice,
                finalPrice,
                discountAmount,
                discountPercentage,
                appliedRules,
                generatePriceReason(appliedRules),
                !originalPrice.equals(finalPrice)
            );
            
        } catch (NumberFormatException e) {
            logger.error("Error parsing price for product {}: {}", product.getId(), product.getPrice());
            return createErrorPriceDTO(product);
        }
    }
    
    @Override
    public DynamicPriceDTO calculateDynamicPrice(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return calculateDynamicPrice(product);
    }
    
    @Override
    public List<DynamicPriceDTO> calculateDynamicPricesForProducts(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        return products.stream()
            .map(this::calculateDynamicPrice)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateAllProductPrices() {
        logger.info("Starting bulk price update for all products");
        List<Product> products = productRepository.findAll();
        
        for (Product product : products) {
            try {
                DynamicPriceDTO priceDTO = calculateDynamicPrice(product);
                if (priceDTO.isPriceChanged()) {
                    logger.debug("Updating price for product {}: {} -> {}", 
                        product.getId(), priceDTO.getOriginalPrice(), priceDTO.getDynamicPrice());
                }
            } catch (Exception e) {
                logger.error("Error updating price for product {}: {}", product.getId(), e.getMessage());
            }
        }
        
        logger.info("Completed bulk price update for {} products", products.size());
    }
    
    @Override
    @Transactional
    public void updateProductPricesForCategory(Long categoryId) {
        logger.info("Starting price update for category {}", categoryId);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, Pageable.unpaged());
        List<Product> products = productPage.getContent();
        
        for (Product product : products) {
            try {
                DynamicPriceDTO priceDTO = calculateDynamicPrice(product);
                if (priceDTO.isPriceChanged()) {
                    logger.debug("Updating price for product {} in category {}: {} -> {}", 
                        product.getId(), categoryId, priceDTO.getOriginalPrice(), priceDTO.getDynamicPrice());
                }
            } catch (Exception e) {
                logger.error("Error updating price for product {} in category {}: {}", 
                    product.getId(), categoryId, e.getMessage());
            }
        }
        
        logger.info("Completed price update for {} products in category {}", products.size(), categoryId);
    }
    
    @Override
    public BigDecimal getEffectivePrice(Product product) {
        DynamicPriceDTO priceDTO = calculateDynamicPrice(product);
        return priceDTO.getDynamicPrice();
    }
    
    @Override
    public String checkSpecialDay() {
        LocalDate today = LocalDate.now(VIETNAM_TIMEZONE);
        
        if (today.getMonthValue() == 2 && today.getDayOfMonth() == 14) {
            return "VALENTINE_DAY";
        } else if (today.getMonthValue() == 3 && today.getDayOfMonth() == 8) {
            return "WOMEN_DAY";
        } else if (today.getMonthValue() == 12 && today.getDayOfMonth() == 25) {
            return "CHRISTMAS";
        } else if (today.getMonthValue() == 1 && today.getDayOfMonth() == 1) {
            return "NEW_YEAR";
        } else if (today.getMonthValue() == 5 && today.getDayOfMonth() >= 8 && today.getDayOfMonth() <= 14) {
            return "MOTHERS_DAY";
        }
        
        return null;
    }
    
    private List<PricingRule> getApplicableRules(Product product) {
        List<PricingRule> rules = new ArrayList<>();
        
        rules.addAll(pricingRuleRepository.findByProductIdAndIsActiveTrueOrderByPriorityAsc(product.getId()));
        rules.addAll(pricingRuleRepository.findByCategoryIdAndIsActiveTrueOrderByPriorityAsc(product.getCategory().getId()));
        rules.addAll(pricingRuleRepository.findByApplyToAllProductsTrueAndIsActiveTrueOrderByPriorityAsc());
        
        return rules.stream()
            .distinct()
            .sorted((r1, r2) -> r1.getPriority().compareTo(r2.getPriority()))
            .collect(Collectors.toList());
    }
    
    private boolean isRuleApplicable(PricingRule rule, Product product) {
        logger.debug("Rule information: ruleName={}, triggerType={}, isActive={}", 
            rule.getRuleName(), rule.getTriggerType(), rule.getIsActive());
        if (!rule.getIsActive()) {
            return false;
        }
        
        switch (rule.getTriggerType()) {
            case TIME_OF_DAY:
                return isTimeOfDayApplicable(rule);
            case PRODUCT_CONDITION:
                return isProductConditionApplicable(rule, product);
            case SPECIAL_DAY:
                return isSpecialDayApplicable(rule);
            case COMBINED:
                return isTimeOfDayApplicable(rule) || isProductConditionApplicable(rule, product) || 
                       isSpecialDayApplicable(rule);
            default:
                return true;
        }
    }
    
    private boolean isTimeOfDayApplicable(PricingRule rule) {
        
        if (rule.getStartTime() == null || rule.getEndTime() == null) {
            return true;
        }
        
        LocalTime now = LocalTime.now(VIETNAM_TIMEZONE);
        LocalTime start = rule.getStartTime();
        LocalTime end = rule.getEndTime();

        logger.debug("Time's rule: {}", rule.getStartTime() + " - " + rule.getEndTime());
        logger.debug("Current time: {}", now);
        
        if (start.isBefore(end)) { 
            return !now.isBefore(start) && !now.isAfter(end);
        } else {
            return !now.isBefore(start) || !now.isAfter(end);
        }
    }
    
    private boolean isProductConditionApplicable(PricingRule rule, Product product) {
        if (rule.getProductCondition() == null) {
            return true;
        }
        
        if (product.getStatus() == null) {
            return false;
        }
        
        switch (rule.getProductCondition()) {
            case NEW:
                return product.getStatus() == ProductStatus.NEW_FLOWER;
            case OLD:
                return product.getStatus() == ProductStatus.OLD_FLOWER;
            case EXPIRING_SOON:
                return product.getStatus() == ProductStatus.LOW_STOCK;
            default:
                return true;
        }
    }
    
    private boolean isSpecialDayApplicable(PricingRule rule) {
        if (rule.getSpecialDayName() == null) {
            return true;
        }
        
        String currentSpecialDay = checkSpecialDay();
        return rule.getSpecialDayName().equals(currentSpecialDay);
    }
    
    private BigDecimal applyPricingRule(BigDecimal currentPrice, PricingRule rule) {
        BigDecimal newPrice = currentPrice;
        
        switch (rule.getType()) {
            case PERCENTAGE:
                BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    rule.getModifierValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                newPrice = currentPrice.multiply(discountMultiplier);
                break;
            case FIXED:
                newPrice = currentPrice.subtract(rule.getModifierValue());
                break;
            case DECAY:
                BigDecimal decayFactor = rule.getModifierValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                newPrice = currentPrice.multiply(BigDecimal.ONE.subtract(decayFactor));
                break;
        }

        logger.debug("Applying rule {}: current price {}, new price {}", 
            rule.getRuleName(), currentPrice, newPrice);
        
        if (rule.getMaxDiscountAmount() != null) {
            BigDecimal discount = currentPrice.subtract(newPrice);
            if (discount.compareTo(rule.getMaxDiscountAmount()) > 0) {
                newPrice = currentPrice.subtract(rule.getMaxDiscountAmount());
            }
        }

        logger.debug("Final price after rule {}: {}", rule.getRuleName(), newPrice);
        
        return newPrice.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
    
    private String generatePriceReason(List<String> appliedRules) {
        if (appliedRules.isEmpty()) {
            return "Regular pricing";
        } else if (appliedRules.size() == 1) {
            return "Special pricing due to: " + appliedRules.get(0);
        } else {
            return "Special pricing due to multiple rules: " + String.join(", ", appliedRules);
        }
    }
    
    private DynamicPriceDTO createErrorPriceDTO(Product product) {
        return new DynamicPriceDTO(
            product.getId(),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            0.0,
            List.of("Price calculation error"),
            "Error calculating price",
            false
        );
    }
}
