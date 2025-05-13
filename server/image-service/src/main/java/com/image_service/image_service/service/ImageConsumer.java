package com.image_service.image_service.service;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.image_service.image_service.DTOs.ImageUploadRequest;
import com.image_service.image_service.model.images;
import com.image_service.image_service.repository.ImageRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageConsumer {
    @Autowired
    private S3Service s3Service;
    @Autowired
    private ImageRepository imageRepository;

    @RabbitListener(queues = "image.upload.queue")
    public void receiveMessage(ImageUploadRequest request) {
        System.out.println("Received message: " + request);
        try {
            Map<String, String> uploadResult = s3Service.uploadFile(request.getTempFilePath());
            System.out.println("Uploaded image to S3: " + uploadResult);

            // Save to database
            images savedImage = saveImageToDatabase(uploadResult);
            System.out.println("Saved image to database: " + savedImage);

        } catch (Exception e) {
            System.out.println("Error uploading image to S3: " + e.getMessage());
        }
    }

    private images saveImageToDatabase(Map<String, String> uploadResult) {
        System.out.println("Saving image to database: " + uploadResult);
        images image = new images();
        image.setUrl(uploadResult.get("url"));
        image.setPublicId(uploadResult.get("public_id"));
        return imageRepository.save(image);
    }
}
