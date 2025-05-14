package com.image_service.image_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.image_service.image_service.DTOs.ImageResponse;
import com.image_service.image_service.model.images;
import com.image_service.image_service.repository.ImageRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final S3Service s3Service;
    private final ImageProducer imageProducer;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(S3Service s3Service, ImageProducer imageProducer, ImageRepository imageRepository) {
        this.s3Service = s3Service;
        this.imageProducer = imageProducer;
        this.imageRepository = imageRepository;
    }

    public ImageResponse uploadImage(MultipartFile file) throws IOException {
        Map<String, String> uploadResult = s3Service.uploadFile(file);

        String requestId = UUID.randomUUID().toString();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("requestId", requestId);
        messageData.put("url", uploadResult.get("url"));
        messageData.put("publicId", uploadResult.get("public_id"));

        imageProducer.sendImageToQueue(messageData);

        return ImageResponse.builder()
                .url(uploadResult.get("url"))
                .publicId(uploadResult.get("public_id"))
                .build();
    }

    public Optional<ImageResponse> getImage(Long publicId) {
        return imageRepository.findByPublicId(publicId)
                .map(image -> ImageResponse.builder()
                        .id(image.getId())
                        .url(image.getUrl())
                        .publicId(image.getPublicId().toString())
                        .build());
    }

    public ImageResponse saveImageToDatabase(Map<String, Object> imageData) {
        String url = (String) imageData.get("url");
        Long publicId = (Long) imageData.get("publicId");

        images image = new images();
        image.setUrl(url);
        image.setPublicId(publicId);

        images savedImage = imageRepository.save(image);

        return ImageResponse.builder()
                .id(savedImage.getId())
                .url(savedImage.getUrl())
                .publicId(savedImage.getPublicId().toString())
                .build();
    }
}