package com.catalog.catalog_service.repository;

import com.catalog.catalog_service.model.product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface productRepo extends JpaRepository<product, Long> {

}
