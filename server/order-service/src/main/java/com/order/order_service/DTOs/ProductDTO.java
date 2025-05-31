package com.order.order_service.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
  
        private Long id;
        private String name;
        private String description;
        private double price;
        private Long categoryId;
        private List<Long> imageIds;
        private List<String> imageUrls;
    
  
}
