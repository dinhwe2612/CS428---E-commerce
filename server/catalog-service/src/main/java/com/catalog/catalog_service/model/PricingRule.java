package com.catalog.catalog_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pricing_rules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricingRule {
    public enum RuleType {
        PERCENTAGE,
        FIXED,
        DECAY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "end_date", nullable = false)
    private String endDate;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleType type;

    @Column(name = "modifierValue", nullable = false)
    private Double modifierValue;

    @Column(name = "order_apply")
    private Integer orderApply;

    @Version
    private Long version;
}
