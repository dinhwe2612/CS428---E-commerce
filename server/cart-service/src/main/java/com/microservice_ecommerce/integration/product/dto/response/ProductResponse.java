package com.microservice_ecommerce.integration.product.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Boolean available;

} 