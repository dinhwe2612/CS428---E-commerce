package com.catalog.catalog_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private double price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private category category;
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private inventory inventory;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "product_image_ids",
        joinColumns = @JoinColumn(name = "product_id", nullable = false),
        foreignKey = @ForeignKey(name = "fk_product_image_ids")
    )
    @Column(name = "image_id", nullable = false)
    @OrderColumn(name = "image_order")
    private List<Long> imageIds;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "product_image_urls",
        joinColumns = @JoinColumn(name = "product_id", nullable = false),
        foreignKey = @ForeignKey(name = "fk_product_image_urls")
    )
    @Column(name = "image_url", nullable = false)
    @OrderColumn(name = "url_order")
    private List<String> imageUrls;
}
