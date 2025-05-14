package com.image_service.image_service.service;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.image_service.image_service.DTOs.ImageResponse;
import com.image_service.image_service.config.RabbitConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageConsumer {

    private final ImageService imageService;

    @RabbitListener(queues = RabbitConfig.UPLOAD_QUEUE)
    public void processImageMessage(Map<String, Object> imageData) {
        try {
            String requestId = (String) imageData.get("requestId");
            log.info("Processing image message: requestId={}", requestId);
            
            ImageResponse savedImage = imageService.saveImageToDatabase(imageData);
            
            log.info("Image saved to database: id={}, url={}, publicId={}", 
                    savedImage.getId(), savedImage.getUrl(), savedImage.getPublicId());
        } catch (Exception e) {
            log.error("Error processing image message: {}", e.getMessage(), e);
        }
    }
}
