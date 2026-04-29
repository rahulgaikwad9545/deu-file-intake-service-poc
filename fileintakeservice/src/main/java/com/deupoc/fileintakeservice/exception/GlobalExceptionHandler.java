package com.deupoc.fileintakeservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the File Intake Service.
 * Handles various exceptions and returns appropriate HTTP responses with detailed error information.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle FileNotFoundException with detailed error response.
     *
     * @param ex the exception
     * @return ResponseEntity with NOT_FOUND status and error details
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFileNotFound(FileNotFoundException ex) {
        log.warn("File not found: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "File not found");
        error.put("message", ex.getMessage());
        error.put("type", "FileNotFoundException");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle FileOperationException with detailed error response.
     *
     * @param ex the exception
     * @return ResponseEntity with INTERNAL_SERVER_ERROR status and error details
     */
    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<Map<String, String>> handleFileOperation(FileOperationException ex) {
        log.error("File operation failed: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "File operation failed");
        error.put("message", ex.getMessage());
        error.put("type", "FileOperationException");
        if (ex.getCause() != null) {
            error.put("cause", ex.getCause().getClass().getSimpleName());
            error.put("causeMessage", ex.getCause().getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle IllegalArgumentException with detailed error response.
     *
     * @param ex the exception
     * @return ResponseEntity with BAD_REQUEST status and error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid request");
        error.put("message", ex.getMessage());
        error.put("type", "IllegalArgumentException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle MaxUploadSizeExceededException for file size limits.
     *
     * @param ex the exception
     * @return ResponseEntity with BAD_REQUEST status and error details
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        log.warn("File size exceeded: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "File size exceeds maximum allowed limit");
        error.put("message", ex.getMessage());
        error.put("type", "MaxUploadSizeExceededException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle generic exceptions with detailed error response.
     *
     * @param ex the exception
     * @return ResponseEntity with INTERNAL_SERVER_ERROR status and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred");
        error.put("message", ex.getMessage());
        error.put("type", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}