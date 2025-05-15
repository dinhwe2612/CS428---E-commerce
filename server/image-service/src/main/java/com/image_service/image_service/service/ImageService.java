package com.image_service.image_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.image_service.image_service.DTOs.ImageUploadResponse;
import com.image_service.image_service.model.images;
import com.image_service.image_service.repository.ImageRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class ImageService {

    private final S3Service s3Service;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(S3Service s3Service, ImageRepository imageRepository) {
        this.s3Service = s3Service;
        this.imageRepository = imageRepository;
    }

    public ImageUploadResponse uploadImage(MultipartFile file) throws IOException {
        Map<String, String> uploadResult = s3Service.uploadFile(file);
        String url = uploadResult.get("url");
        String publicId = uploadResult.get("public_id");

        images image = new images();
        image.setUrl(url);
        image.setPublicId(publicId);
        images savedImage = imageRepository.save(image);

        return ImageUploadResponse.builder()
                .id(savedImage.getId())
                .url(savedImage.getUrl())
                .build();
    }

    public Optional<ImageUploadResponse> getImage(Long id) {
        return imageRepository.findById(id)
                .map(image -> ImageUploadResponse.builder()
                        .id(image.getId())
                        .url(image.getUrl())
                        .build());
    }
}