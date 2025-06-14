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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/images")
@Tag(name = "Images", description = "Endpoints for uploading and retrieving images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(
            summary = "Upload an image",
            description = "Accepts a multipart/form-data request to upload an image file"
    )
    @RequestBody(
            description = "Multipart form data with the image file",
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ImageUploadRequest.class)
            )
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
            @Parameter(hidden = true)
            @ModelAttribute ImageUploadRequest request
    ) {
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

    @Operation(
            summary = "Retrieve an image by ID",
            description = "Fetches the metadata and download URL of an uploaded image by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> getImage(
            @Parameter(description = "ID of the image to retrieve", example = "123", required = true)
            @PathVariable Long id
    ) {
        return imageService.getImage(id)
                .map(image -> ResponseEntity.ok(new ApiResponse<>(true, "Image found", image)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Image not found", null)));
    }
}
