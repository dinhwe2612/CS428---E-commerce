
package com.catalog.catalog_service.RabbitMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    

    private Long id;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
    private String inventoryId;

}
