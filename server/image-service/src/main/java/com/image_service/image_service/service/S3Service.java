package com.image_service.image_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public Map<String, String> uploadFile(String filePath) throws Exception {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new Exception("File not found: " + filePath);
            }

            String fileExtension = getFileExtension(filePath);
            String key = generateUniqueFileName(fileExtension);
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(determineContentType(fileExtension))
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromBytes(Files.readAllBytes(Paths.get(filePath))));

            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("public_id", key);
            
            return result;
        } catch (Exception e) {
            throw new Exception("Error uploading file to S3: " + e.getMessage(), e);
        }
    }
    
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + extension;
    }
    
    private String getFileExtension(String filePath) {
        int lastDotPosition = filePath.lastIndexOf('.');
        if (lastDotPosition > 0) {
            return filePath.substring(lastDotPosition);
        }
        return "";
    }
    
    private String determineContentType(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".webp":
                return "image/webp";
            case ".bmp":
                return "image/bmp";
            default:
                return "application/octet-stream";
        }
    }
} 