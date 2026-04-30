package com.deupoc.fileintakeservice.service;

import com.deupoc.fileintakeservice.exception.FileNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

/**
 * Service for handling AWS S3 operations including blob and clob uploads, download, and delete.
 * This service manages file storage in AWS S3 and maintains metadata in the database.
 */
@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client){
        this.s3Client=s3Client;
    }
    @Override
    public String toString() {
        return "S3Service{" +
                "bucketName='" + bucketName + '\'' +
                '}';
    }

    /**
     * Logs the S3 configuration during bean initialization
     */
    @PostConstruct
    public void init() {
        log.info("S3Service initialized with bucket: {}", bucketName);
        if (bucketName == null || bucketName.isEmpty()) {
            log.warn("WARNING: aws.s3.bucket-name property not configured!");
        }
    }

  /**
     * Uploads a file to the configured S3 bucket.
     *
     * @param multipartFile the file to upload
     * @return PutObjectResponse containing details of the upload operation
     * @throws IOException if an I/O error occurs during file upload
     */
    public PutObjectResponse uploadFile(MultipartFile multipartFile) throws IOException {

       return s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(multipartFile.getOriginalFilename())
                .build(), RequestBody.fromBytes(multipartFile.getBytes()));
    }


    /**
     * Download a file from S3 bucket
     */
    public byte[] downloadFile(String key) {
        try {
            return s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build())
                    .asByteArray();
        } catch (Exception e) {
            throw new FileNotFoundException("File not found in S3: " + key, e);
        }
    }




}

