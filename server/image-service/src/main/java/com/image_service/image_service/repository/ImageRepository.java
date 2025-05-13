package com.image_service.image_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.image_service.image_service.model.images;

public interface ImageRepository extends JpaRepository<images, Long> {

}
