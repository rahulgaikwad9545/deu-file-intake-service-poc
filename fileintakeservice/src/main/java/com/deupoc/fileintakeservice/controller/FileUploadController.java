package com.deupoc.fileintakeservice.controller;

import com.deupoc.fileintakeservice.exception.FileOperationException;
import com.deupoc.fileintakeservice.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for file import/export operations.
 * Provides endpoints for uploading (importing) files to storage,
 * downloading (exporting) files from storage, and managing file metadata.
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://localhost:5173"})
@Tag(name = "File Operations", description = "API for file import/export operations")
public class FileUploadController {

    private final S3Service s3Service;

    public FileUploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Import (upload) a file to the storage system.
     * The file is stored in AWS S3 and metadata is saved to the database.
     *
     * @param file the multipart file to upload
     * @return ResponseEntity containing the file metadata or error details
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File imported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or request"),
            @ApiResponse(responseCode = "500", description = "Internal server error during file import")
    })
    public ResponseEntity<String> importFile(
            @Parameter(description = "File to import", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                log.warn("Attempt to import empty file");
                Map<String, String> error = new HashMap<>();
                error.put("error", "File is empty");
                return ResponseEntity.badRequest().body(error.toString());
            }

            log.info("Importing file: {} (size: {} bytes, type: {})",
                    file.getOriginalFilename(), file.getSize(), file.getContentType());

            s3Service.uploadFile(file);
            log.info("File imported successfully: {}", file.getOriginalFilename());
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());

        } catch (FileOperationException e) {
            log.error("File operation failed for: {}", file.getOriginalFilename(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "File operation failed");
            error.put("message", e.getMessage());
            error.put("cause", e.getCause() != null ? e.getCause().getClass().getSimpleName() : "Unknown");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
        } catch (IOException e) {
            log.error("IO error during file upload: {}", file.getOriginalFilename(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "IO error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", file.getOriginalFilename(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unexpected error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        byte[] fileContent = s3Service.downloadFile(fileName);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    @GetMapping("/health")
    @Operation(summary = "Service health check", description = "Check service status and configuration")
    @ApiResponse(responseCode = "200", description = "Service health status")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "File Intake Service");

        // Add service information
        Map<String, String> info = new HashMap<>();
        info.put("version", "1.0.0");
        info.put("description", "File import/export service with AWS S3 and metadata storage");
        health.put("info", info);

        log.debug("Health check requested");
        return ResponseEntity.ok(health);
    }
}