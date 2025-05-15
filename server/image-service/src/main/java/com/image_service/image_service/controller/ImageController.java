package com.image_service.image_service.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.image_service.image_service.DTOs.ApiResponse;
import com.image_service.image_service.DTOs.ImageUploadRequest;
import com.image_service.image_service.DTOs.ImageUploadResponse;
import com.image_service.image_service.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(@ModelAttribute ImageUploadRequest request) {
        try {
            MultipartFile file = request.getFile();
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "File cannot be empty", null)
                );
            }
            
            ImageUploadResponse response = imageService.uploadImage(file);
            ApiResponse<ImageUploadResponse> apiResponse = new ApiResponse<>(
                true,
                "Successfully uploaded image",
                response
            );
            
            return ResponseEntity.ok(apiResponse);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to upload image: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> getImage(@PathVariable Long id) {
        return imageService.getImage(id)
            .map(image -> ResponseEntity.ok(new ApiResponse<>(true, "Image found", image)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Image not found", null)));
    }
}
