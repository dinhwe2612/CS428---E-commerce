package com.image_service.image_service.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.image_service.image_service.model.images;
import com.image_service.image_service.repository.ImageRepository;
import com.image_service.image_service.service.ImageProducer;
import com.image_service.image_service.DTOs.ImageUploadRequest;
import com.image_service.image_service.config.RabbitConfig;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
@NoArgsConstructor
public class ImageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ImageRepository imageRepository;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Long>> uploadImage(@RequestBody ImageUploadRequest request) {
        // Generate a unique request ID
        Long requestId = System.currentTimeMillis();

        // Send to RabbitMQ and return immediately
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                request);

        // Return the request ID immediately
        return ResponseEntity.ok(Map.of("requestId", requestId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getImage(@PathVariable Long id) {
        images image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return ResponseEntity.ok(image.getUrl());
    }
}
