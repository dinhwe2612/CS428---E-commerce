package com.catalog.catalog_service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.catalog_service.dto.DynamicPriceDTO;
import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.PricingRuleDTO;
import com.catalog.catalog_service.dto.request.CreatePricingRuleRequest;
import com.catalog.catalog_service.dto.request.UpdatePricingRuleRequest;
import com.catalog.catalog_service.model.PricingRule;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.repository.jpa.PricingRuleRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;
import com.catalog.catalog_service.service.DynamicPricingService;
import com.catalog.catalog_service.service.PricingRuleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dynamic-pricing")
@Tag(name = "Dynamic Pricing", description = "Endpoints for managing dynamic pricing rules and calculating prices")
@RequiredArgsConstructor
public class DynamicPricingController {
    
    private final DynamicPricingService dynamicPricingService;
    private final PricingRuleService pricingRuleService;
    private final ProductRepository productRepository;
    private final PricingRuleRepository pricingRuleRepository;
    
    @Operation(
        summary = "Calculate dynamic price for a product",
        description = "Calculates the dynamic price for a specific product based on active pricing rules"
    )
    @ApiResponse(responseCode = "200", description = "Dynamic price calculated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DynamicPriceDTO.class)))
    @GetMapping("/products/{productId}/price")
    public ResponseEntity<DynamicPriceDTO> calculateDynamicPrice(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long productId
    ) {
        DynamicPriceDTO price = dynamicPricingService.calculateDynamicPrice(productId);
        return ResponseEntity.ok(price);
    }
    
    @Operation(
        summary = "Calculate dynamic prices for multiple products",
        description = "Calculates dynamic prices for a list of products"
    )
    @ApiResponse(responseCode = "200", description = "Dynamic prices calculated successfully")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "List of product IDs for price calculation",
        required = true,
        content = @Content(
            mediaType = "application/json", 
            schema = @Schema(
                type = "array", 
                description = "Array of product IDs",
                example = "[1, 2, 3]"
            )
        )
    )
    @PostMapping("/products/prices")
    public ResponseEntity<List<DynamicPriceDTO>> calculateDynamicPrices(
            @RequestBody List<Long> productIds  
    ) {
        List<DynamicPriceDTO> prices = dynamicPricingService.calculateDynamicPricesForProducts(productIds);
        return ResponseEntity.ok(prices);
    }
    
    
    @Operation(
        summary = "Check current special day",
        description = "Returns the current special day if any (Valentine's Day, Women's Day, etc.)"
    )
    @ApiResponse(responseCode = "200", description = "Current special day information")
    @GetMapping("/special-day")
    public ResponseEntity<String> checkSpecialDay() {
        String specialDay = dynamicPricingService.checkSpecialDay();
        return ResponseEntity.ok(specialDay != null ? specialDay : "No special day today");
    }
    
    @Operation(
        summary = "List all pricing rules",
        description = "Retrieves a paginated list of pricing rules with optional filters"
    )
    @ApiResponse(responseCode = "200", description = "Page of pricing rules returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageDTO.class)))
    @GetMapping("/rules")
    public ResponseEntity<PageDTO<PricingRuleDTO>> getAllPricingRules(
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by rule name")
            @RequestParam(required = false) String ruleName,
            @Parameter(description = "Filter by active status")
            @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Filter by trigger type")
            @RequestParam(required = false) PricingRule.TriggerType triggerType,
            @Parameter(description = "Field to sort by", example = "priority")
            @RequestParam(defaultValue = "priority") String sortBy,
            @Parameter(description = "Sort direction (asc|desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageDTO<PricingRuleDTO> rules = pricingRuleService.getAllPricingRules(pageable, ruleName, isActive, triggerType);
        return ResponseEntity.ok(rules);
    }
    
    @Operation(
        summary = "Get pricing rule by ID",
        description = "Retrieves a specific pricing rule by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Pricing rule found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PricingRuleDTO.class)))
    @GetMapping("/rules/{id}")
    public ResponseEntity<PricingRuleDTO> getPricingRuleById(
            @Parameter(description = "Pricing rule ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        PricingRuleDTO rule = pricingRuleService.getPricingRuleById(id);
        return ResponseEntity.ok(rule);
    }
    
    @Operation(
        summary = "Create a new pricing rule",
        description = "Creates a new dynamic pricing rule (Admin only)"
    )
    @ApiResponse(responseCode = "201", description = "Pricing rule created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PricingRuleDTO.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Pricing rule creation request with all required fields",
        required = true,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatePricingRuleRequest.class))
    )
    @PostMapping("/rules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingRuleDTO> createPricingRule(
            @Valid @RequestBody CreatePricingRuleRequest request
    ) {
        PricingRuleDTO rule = pricingRuleService.createPricingRule(request);
        return ResponseEntity.status(201).body(rule);
    }
    
    @Operation(
        summary = "Update a pricing rule",
        description = "Updates an existing pricing rule (Admin only)"
    )
    @ApiResponse(responseCode = "200", description = "Pricing rule updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PricingRuleDTO.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Pricing rule update request with fields to modify",
        required = true,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdatePricingRuleRequest.class))
    )
    @PutMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingRuleDTO> updatePricingRule(
            @Parameter(description = "Pricing rule ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdatePricingRuleRequest request
    ) {
        PricingRuleDTO rule = pricingRuleService.updatePricingRule(id, request);
        return ResponseEntity.ok(rule);
    }
    
    @Operation(
        summary = "Delete a pricing rule",
        description = "Deletes a pricing rule by ID (Admin only)"
    )
    @ApiResponse(responseCode = "204", description = "Pricing rule deleted")
    @DeleteMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePricingRule(
            @Parameter(description = "Pricing rule ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        pricingRuleService.deletePricingRule(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Activate a pricing rule",
        description = "Activates a pricing rule by ID (Admin only)"
    )
    @ApiResponse(responseCode = "200", description = "Pricing rule activated")
    @PostMapping("/rules/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activatePricingRule(
            @Parameter(description = "Pricing rule ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        pricingRuleService.activatePricingRule(id);
        return ResponseEntity.ok("Pricing rule activated successfully");
    }
    
    @Operation(
        summary = "Deactivate a pricing rule",
        description = "Deactivates a pricing rule by ID (Admin only)"
    )
    @ApiResponse(responseCode = "200", description = "Pricing rule deactivated")
    @PostMapping("/rules/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivatePricingRule(
            @Parameter(description = "Pricing rule ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        pricingRuleService.deactivatePricingRule(id);
        return ResponseEntity.ok("Pricing rule deactivated successfully");
    }
    
    @Operation(
        summary = "Get active pricing rules",
        description = "Retrieves all active pricing rules ordered by priority"
    )
    @ApiResponse(responseCode = "200", description = "Active pricing rules returned")
    @GetMapping("/rules/active")
    public ResponseEntity<List<PricingRuleDTO>> getActivePricingRules() {
        List<PricingRuleDTO> rules = pricingRuleService.getActivePricingRules();
        return ResponseEntity.ok(rules);
    }
    
    @Operation(
        summary = "Get pricing rules for a product",
        description = "Retrieves all pricing rules applicable to a specific product"
    )
    @ApiResponse(responseCode = "200", description = "Product pricing rules returned")
    @GetMapping("/products/{productId}/rules")
    public ResponseEntity<List<PricingRuleDTO>> getPricingRulesByProductId(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long productId
    ) {
        List<PricingRuleDTO> rules = pricingRuleService.getPricingRulesByProductId(productId);
        return ResponseEntity.ok(rules);
    }
    
    @Operation(
        summary = "Get pricing rules for a category",
        description = "Retrieves all pricing rules applicable to a specific category"
    )
    @ApiResponse(responseCode = "200", description = "Category pricing rules returned")
    @GetMapping("/categories/{categoryId}/rules")
    public ResponseEntity<List<PricingRuleDTO>> getPricingRulesByCategoryId(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId
    ) {
        List<PricingRuleDTO> rules = pricingRuleService.getPricingRulesByCategoryId(categoryId);
        return ResponseEntity.ok(rules);
    }
    
    @Operation(
        summary = "Debug: Get applicable rules for a product",
        description = "Debug endpoint to check which rules apply to a specific product"
    )
    @GetMapping("/debug/products/{productId}/applicable-rules")
    public ResponseEntity<Map<String, Object>> getApplicableRulesForProduct(
            @Parameter(description = "Product ID", required = true, example = "3")
            @PathVariable Long productId
    ) {
        Map<String, Object> result = new HashMap<>();
        
        // Get product
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            result.put("error", "Product not found");
            return ResponseEntity.ok(result);
        }
        
        // Get all active rules
        List<PricingRule> allRules = pricingRuleRepository.findByIsActiveTrueOrderByPriorityAsc();
        
        result.put("productId", productId);
        result.put("categoryId", product.getCategory().getId());
        result.put("totalActiveRules", allRules.size());
        
        List<Map<String, Object>> ruleDetails = new ArrayList<>();
        for (PricingRule rule : allRules) {
            Map<String, Object> ruleInfo = new HashMap<>();
            ruleInfo.put("id", rule.getId());
            ruleInfo.put("name", rule.getRuleName());
            ruleInfo.put("applyToAll", rule.getApplyToAllProducts());
            
            // Check products
            List<Long> ruleProductIds = new ArrayList<>();
            if (rule.getPricingRuleProducts() != null) {
                ruleProductIds = rule.getPricingRuleProducts().stream()
                    .map(prp -> prp.getProduct().getId())
                    .collect(Collectors.toList());
            }
            ruleInfo.put("productIds", ruleProductIds);
            
            // Check categories  
            List<Long> ruleCategoryIds = new ArrayList<>();
            if (rule.getPricingRuleCategories() != null) {
                ruleCategoryIds = rule.getPricingRuleCategories().stream()
                    .map(prc -> prc.getCategoryId())
                    .collect(Collectors.toList());
            }
            ruleInfo.put("categoryIds", ruleCategoryIds);
            
            ruleDetails.add(ruleInfo);
        }
        
        result.put("rules", ruleDetails);
        return ResponseEntity.ok(result);
    }
    
    @Operation(
        summary = "Debug: Get rule details by ID",
        description = "Debug endpoint to check rule relationships"
    )
    @GetMapping("/debug/rules/{ruleId}")
    public ResponseEntity<Map<String, Object>> getRuleDetails(
            @Parameter(description = "Rule ID", required = true, example = "1")
            @PathVariable Long ruleId
    ) {
        Map<String, Object> result = new HashMap<>();
        
        PricingRule rule = pricingRuleRepository.findByIdWithRelationships(ruleId).orElse(null);
        if (rule == null) {
            result.put("error", "Rule not found");
            return ResponseEntity.ok(result);
        }
        
        result.put("ruleId", ruleId);
        result.put("ruleName", rule.getRuleName());
        result.put("applyToAll", rule.getApplyToAllProducts());
        
        // Check products
        List<Long> ruleProductIds = new ArrayList<>();
        if (rule.getPricingRuleProducts() != null) {
            ruleProductIds = rule.getPricingRuleProducts().stream()
                .map(prp -> prp.getProduct().getId())
                .collect(Collectors.toList());
        }
        result.put("productIds", ruleProductIds);
        
        // Check categories  
        List<Long> ruleCategoryIds = new ArrayList<>();
        if (rule.getPricingRuleCategories() != null) {
            ruleCategoryIds = rule.getPricingRuleCategories().stream()
                .map(prc -> prc.getCategoryId())
                .collect(Collectors.toList());
        }
        result.put("categoryIds", ruleCategoryIds);
        
        return ResponseEntity.ok(result);
    }
}
