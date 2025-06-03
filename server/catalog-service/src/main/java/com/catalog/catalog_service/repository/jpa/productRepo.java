package com.catalog.catalog_service.repository.jpa;

import com.catalog.catalog_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface productRepo extends JpaRepository<Product, Long> {

}
