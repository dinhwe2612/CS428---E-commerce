package com.catalog.catalog_service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "arrival_date", nullable = false)
    private LocalDateTime arrivalDate;

    @Column(name = "description")
    private String description;
    
    @Version
    private Long version;

    @OneToMany(
            mappedBy = "inventory",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PricingRule> pricingRules = new ArrayList<>();
    
    // Helper method to ensure pricingRules is never null
    public List<PricingRule> getPricingRules() {
        if (pricingRules == null) {
            pricingRules = new ArrayList<>();
        }
        return pricingRules;
    }
    
    public void setPricingRules(List<PricingRule> pricingRules) {
        this.pricingRules = pricingRules != null ? pricingRules : new ArrayList<>();
    }
}
