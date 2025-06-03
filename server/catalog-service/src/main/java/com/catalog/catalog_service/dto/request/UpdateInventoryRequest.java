package com.catalog.catalog_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInventoryRequest {
    private Long productId;
    @PositiveOrZero(message = "Quantity must be greater than or equal to 0")
    private Integer quantity;
    private LocalDateTime arrivalDate;
    private String description;
}