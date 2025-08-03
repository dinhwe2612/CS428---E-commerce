package com.catalog.catalog_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductPriceUpdateEvent {
    private Long productId;
    private String reason;
}
