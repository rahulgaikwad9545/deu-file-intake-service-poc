package com.deupoc.fileintakeservice.controller;

import com.deupoc.fileintakeservice.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * REST Controller for uploading and managing files in S3.
 * Supports binary blob uploads and text clob uploads.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Management", description = "APIs for uploading files to S3 using blob and clob storage types")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload file to S3", description = "Upload a file to S3 and infer blob/clob storage type from the content type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> uploadFile(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file)
            throws IOException {

        log.info("Uploading file to S3: {}", file.getOriginalFilename());
        String s3Key = fileStorageService.uploadFile(file);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "File uploaded successfully",
                "s3Key", s3Key,
                "fileName", file.getOriginalFilename()
        ));
    }

    @PostMapping("/upload/blob")
    @Operation(summary = "Upload binary blob file to S3", description = "Upload a binary file to S3 as blob storage")
    public ResponseEntity<Map<String, Object>> uploadBlobFile(
            @Parameter(description = "Binary file to upload") @RequestParam("file") MultipartFile file)
            throws IOException {

        log.info("Uploading blob file to S3: {}", file.getOriginalFilename());
        String s3Key = fileStorageService.uploadBlobFile(file);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "storageType", "blob",
                "s3Key", s3Key,
                "fileName", file.getOriginalFilename()
        ));
    }

    @PostMapping("/upload/clob")
    @Operation(summary = "Upload text clob file to S3", description = "Upload a text file to S3 as clob storage")
    public ResponseEntity<Map<String, Object>> uploadClobFile(
            @Parameter(description = "Text file to upload") @RequestParam("file") MultipartFile file)
            throws IOException {

        log.info("Uploading clob file to S3: {}", file.getOriginalFilename());
        String s3Key = fileStorageService.uploadClobFile(file);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "storageType", "clob",
                "s3Key", s3Key,
                "fileName", file.getOriginalFilename()
        ));
    }

    @GetMapping("/download/{key}")
    @Operation(summary = "Download file from S3", description = "Download a file stored in S3 by key")
    public ResponseEntity<byte[]> downloadFile(
            @Parameter(description = "S3 file key") @PathVariable String key) {

        log.info("Downloading file from S3: {}", key);
        byte[] content = fileStorageService.downloadFile(key);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", key);
        headers.setContentLength(content.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }

    @DeleteMapping("/{key}")
    @Operation(summary = "Delete file from S3", description = "Delete a file from S3 by key")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @Parameter(description = "S3 file key") @PathVariable String key) {

        log.info("Deleting file from S3: {}", key);
        fileStorageService.deleteFile(key);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "File deleted successfully",
                "s3Key", key
        ));
    }
}
