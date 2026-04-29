package com.deupoc.fileintakeservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling AWS S3 operations including blob and clob uploads, download, and delete.
 */
@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Upload a binary file to S3 as blob storage
     */
    public String uploadBlobFile(MultipartFile multipartFile) throws IOException {
        validateFile(multipartFile);
        String key = buildKey(multipartFile);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(multipartFile.getContentType())
                .metadata(metadata("blob"))
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(multipartFile.getBytes()));
        return key;
    }

    /**
     * Upload a text file to S3 as clob storage
     */
    public String uploadClobFile(MultipartFile multipartFile) throws IOException {
        validateFile(multipartFile);
        String key = buildKey(multipartFile);
        String text = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(multipartFile.getContentType())
                .metadata(metadata("clob"))
                .build();

        s3Client.putObject(request, RequestBody.fromString(text, StandardCharsets.UTF_8));
        return key;
    }

    /**
     * Upload a file to S3 and infer blob or clob by content type
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        if (isTextFile(multipartFile.getContentType())) {
            return uploadClobFile(multipartFile);
        }
        return uploadBlobFile(multipartFile);
    }

    /**
     * Download a file from S3 bucket
     */
    public byte[] downloadFile(String key) {
        return s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build())
                .asByteArray();
    }

    /**
     * Delete a file from S3 bucket
     */
    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or missing");
        }
    }

    private String buildKey(MultipartFile multipartFile) {
        return multipartFile.getOriginalFilename();
    }

    private Map<String, String> metadata(String storageType) {
        Map<String, String> tags = new HashMap<>();
        tags.put("storage-type", storageType);
        return tags;
    }

    private boolean isTextFile(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("text/") ||
                contentType.equals("application/json") ||
                contentType.equals("application/xml") ||
                contentType.equals("application/csv");
    }
}

