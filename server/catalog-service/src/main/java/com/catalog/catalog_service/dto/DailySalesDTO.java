package com.catalog.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySalesDTO {
    private LocalDate date;
    private Double revenue;
    private Integer orderCount;
    private Integer productsSold;
} 