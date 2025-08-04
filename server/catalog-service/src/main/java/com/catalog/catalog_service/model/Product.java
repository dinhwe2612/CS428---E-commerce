package com.catalog.catalog_service.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "product_path", nullable = false, unique = true)
    private String productPath;

    @Column(name = "name")
    private String name;
    
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "price", length = 50)
    private String price;

    @Column(name = "price_value", insertable = false, updatable = false)
    private BigDecimal priceValue;  

    @Column(name = "description_html", columnDefinition = "TEXT")
    private String descriptionHtml;

    @Column(name = "description_text", columnDefinition = "TEXT")
    private String descriptionText;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ProductImage> images = new ArrayList<>();

    public void addImage(ProductImage image) {
        this.images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        this.images.remove(image);
        image.setProduct(null);
    }
}