package com.deupoc.fileintakeservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Service that handles file uploads and downloads for S3.
 * Supports blob uploads for binary content and clob uploads for text content.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final S3Service s3Service;

    /**
     * Upload a file to S3 and infer content storage type.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        validateFile(file);
        if (isTextFile(file.getContentType())) {
            return uploadClobFile(file);
        }
        return uploadBlobFile(file);
    }

    /**
     * Upload a binary blob file to S3.
     */
    public String uploadBlobFile(MultipartFile file) throws IOException {
        validateFile(file);
        return s3Service.uploadBlobFile(file);
    }

    /**
     * Upload a text CLOB file to S3.
     */
    public String uploadClobFile(MultipartFile file) throws IOException {
        validateFile(file);
        if (!isTextFile(file.getContentType())) {
            throw new IllegalArgumentException("File content type is not text or JSON/XML");
        }
        return s3Service.uploadClobFile(file);
    }

    /**
     * Download a file from S3.
     */
    public byte[] downloadFile(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("S3 key is required");
        }
        return s3Service.downloadFile(key);
    }

    /**
     * Delete a file from S3.
     */
    public void deleteFile(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("S3 key is required");
        }
        s3Service.deleteFile(key);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or missing");
        }
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
