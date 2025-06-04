package com.catalog.catalog_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedEvent extends ProductSyncEvent {
   
    private Long id;


    private String name;

  
    private String description;


    private Double price;

  
    private Long categoryId;

  
    private String categoryName;

  
    private String productPath;

}
