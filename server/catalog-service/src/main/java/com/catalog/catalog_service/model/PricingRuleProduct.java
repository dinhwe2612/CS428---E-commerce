package com.catalog.catalog_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "pricing_rule_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"pricingRule", "product"})
public class PricingRuleProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_rule_id", nullable = false)
    private PricingRule pricingRule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
