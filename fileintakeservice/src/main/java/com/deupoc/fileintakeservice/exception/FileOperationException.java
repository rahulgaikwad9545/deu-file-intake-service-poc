package com.deupoc.fileintakeservice.exception;

/**
 * Exception thrown when a file operation fails.
 * This includes upload, download, or delete operations.
 */
public class FileOperationException extends RuntimeException {

    /**
     * Constructs a new FileOperationException with the specified detail message.
     *
     * @param message the detail message
     */
    public FileOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new FileOperationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}