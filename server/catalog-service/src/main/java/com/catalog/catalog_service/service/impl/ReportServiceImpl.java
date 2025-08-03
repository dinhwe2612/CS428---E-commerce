package com.catalog.catalog_service.service.impl;

import com.catalog.catalog_service.dto.DailySalesDTO;
import com.catalog.catalog_service.dto.ProductSalesDTO;
import com.catalog.catalog_service.dto.SalesReportDTO;
import com.catalog.catalog_service.integration.order.client.OrderServiceClient;
import com.catalog.catalog_service.integration.order.dto.OrderItemResponseDTO;
import com.catalog.catalog_service.integration.order.dto.OrderResponseDTO;
import com.catalog.catalog_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final OrderServiceClient orderServiceClient;

    @Override
    public SalesReportDTO generateSalesReport(LocalDate startDate, LocalDate endDate) {
        List<OrderResponseDTO> completedOrders = orderServiceClient.getOrdersByStatus("COMPLETED");

        List<OrderResponseDTO> filteredOrders = completedOrders.stream()
                .filter(order -> {
                    if (order.getCreatedAt() == null) {
                        return false;
                    }   
                    LocalDate orderDate = order.getCreatedAt().toLocalDate();
                    return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        return createSalesReport(filteredOrders, startDate, endDate);
    }

    @Override
    public SalesReportDTO generateDailySalesReport(LocalDate date) {
        return generateSalesReport(date, date);
    }

    @Override
    public SalesReportDTO generateWeeklySalesReport() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        return generateSalesReport(startDate, endDate);
    }

    @Override
    public SalesReportDTO generateMonthlySalesReport() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.withDayOfMonth(1);
        return generateSalesReport(startDate, endDate);
    }

    @Override
    public List<ProductSalesDTO> getBestSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        List<OrderResponseDTO> completedOrders = orderServiceClient.getOrdersByStatus("COMPLETED");
        
        List<OrderResponseDTO> filteredOrders = completedOrders.stream()
                .filter(order -> {
                    if (order.getCreatedAt() == null) {
                        log.warn("Order with ID {} has null createdAt, skipping from best selling products report", order.getId());
                        return false;
                    }
                    LocalDate orderDate = order.getCreatedAt().toLocalDate();
                    return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        Map<String, ProductSalesData> productSalesMap = new HashMap<>();
        
        filteredOrders.stream()
                .filter(order -> order.getOrderItems() != null)
                .flatMap(order -> order.getOrderItems().stream())
                .forEach(item -> {
                    String productId = item.getProductId();
                    productSalesMap.computeIfAbsent(productId, k -> new ProductSalesData())
                            .addSale(item.getQuantity(), item.getSubtotal(), item.getProductName());
                });

        return productSalesMap.entrySet().stream()
                .map(entry -> {
                    String productId = entry.getKey();
                    ProductSalesData data = entry.getValue();
                    return new ProductSalesDTO(
                            Long.valueOf(productId),
                            data.productName,
                            data.totalQuantity,
                            data.totalRevenue,
                            data.totalRevenue / data.totalQuantity
                    );
                })
                .sorted((p1, p2) -> Integer.compare(p2.getQuantitySold(), p1.getQuantitySold()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private SalesReportDTO createSalesReport(List<OrderResponseDTO> orders, LocalDate startDate, LocalDate endDate) {
        SalesReportDTO report = new SalesReportDTO();
        
        report.setReportGeneratedAt(LocalDateTime.now());
        report.setPeriod(startDate.toString() + " to " + endDate.toString());
        
        double totalRevenue = orders.stream()
                .filter(order -> order.getTotalAmount() != null)
                .mapToDouble(OrderResponseDTO::getTotalAmount)
                .sum();
        report.setTotalRevenue(totalRevenue);
        
        int totalOrders = orders.size();
        report.setTotalOrders(totalOrders);
        
        int totalProductsSold = orders.stream()
                .filter(order -> order.getOrderItems() != null)
                .flatMap(order -> order.getOrderItems().stream())
                .mapToInt(OrderItemResponseDTO::getQuantity)
                .sum();
        report.setTotalProductsSold(totalProductsSold);
        
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;
        report.setAverageOrderValue(averageOrderValue);
        
        Map<String, ProductSalesData> productSalesMap = new HashMap<>();
        
        orders.stream()
                .filter(order -> order.getOrderItems() != null)
                .flatMap(order -> order.getOrderItems().stream())
                .forEach(item -> {
                    String productId = item.getProductId();
                    productSalesMap.computeIfAbsent(productId, k -> new ProductSalesData())
                            .addSale(item.getQuantity(), item.getSubtotal(), item.getProductName());
                });

        List<ProductSalesDTO> topSellingProducts = productSalesMap.entrySet().stream()
                .map(entry -> {
                    String productId = entry.getKey();
                    ProductSalesData data = entry.getValue();
                    return new ProductSalesDTO(
                            Long.valueOf(productId),
                            data.productName,
                            data.totalQuantity,
                            data.totalRevenue,
                            data.totalRevenue / data.totalQuantity
                    );
                })
                .sorted((p1, p2) -> Integer.compare(p2.getQuantitySold(), p1.getQuantitySold()))
                .limit(10)
                .collect(Collectors.toList());
        
        report.setTopSellingProducts(topSellingProducts);
        
        Map<LocalDate, DailySalesData> dailySalesMap = new HashMap<>();
        
        orders.forEach(order -> {
            if (order.getCreatedAt() == null) {
                log.warn("Order with ID {} has null createdAt, skipping from daily sales calculation", order.getId());
                return;
            }
            LocalDate orderDate = order.getCreatedAt().toLocalDate();
            int orderItemsCount = order.getOrderItems() != null ? order.getOrderItems().size() : 0;
            Double orderAmount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
            dailySalesMap.computeIfAbsent(orderDate, k -> new DailySalesData())
                    .addOrder(orderAmount, orderItemsCount);
        });

        List<DailySalesDTO> dailySales = dailySalesMap.entrySet().stream()
                .map(entry -> new DailySalesDTO(
                        entry.getKey(),
                        entry.getValue().revenue,
                        entry.getValue().orderCount,
                        entry.getValue().productsSold
                ))
                .sorted(Comparator.comparing(DailySalesDTO::getDate))
                .collect(Collectors.toList());
        
        report.setDailySales(dailySales);
        
        return report;
    }

    private static class ProductSalesData {
        String productName;
        int totalQuantity = 0;
        double totalRevenue = 0.0;
        
        void addSale(int quantity, double revenue, String name) {
            if (this.productName == null) {
                this.productName = name;
            }
            this.totalQuantity += quantity;
            this.totalRevenue += revenue;
        }
    }

    private static class DailySalesData {
        double revenue = 0.0;
        int orderCount = 0;
        int productsSold = 0;
        
        void addOrder(double orderRevenue, int orderProductCount) {
            this.revenue += orderRevenue;
            this.orderCount++;
            this.productsSold += orderProductCount;
        }
    }
} 