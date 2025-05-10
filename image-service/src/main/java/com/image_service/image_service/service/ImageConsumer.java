package com.image_service.image_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

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
    private Cloudinary cloudinary;
    @Autowired
    private ImageRepository imageRepository;

    @RabbitListener(queues = "image.upload.queue")
    public void receiveMessage(ImageUploadRequest request) {
        System.out.println("Received message: " + request);
        try {
            Map uploadResult = cloudinary.uploader().upload(request.getTempFilePath(), ObjectUtils.emptyMap());
            System.out.println("Uploaded image to Cloudinary: " + uploadResult);

            saveImageToDatabase(uploadResult);
        } catch (Exception e) {
            System.out.println("Error uploading image to Cloudinary: " + e.getMessage());
        }
    }

    private void saveImageToDatabase(Map uploadResult) {
        System.out.println("Saving image to database: " + uploadResult);
        images image = new images();
        image.setUrl(uploadResult.get("url").toString());
        image.setPublicId(uploadResult.get("public_id").toString());
        imageRepository.save(image);

        System.out.println("Image saved to database: " + image);
    }
}
