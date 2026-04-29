package com.deupoc.fileintakeservice.exception;

/**
 * Exception thrown when a requested file is not found.
 * This can occur during download or metadata retrieval operations.
 */
public class FileNotFoundException extends RuntimeException {

    /**
     * Constructs a new FileNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public FileNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new FileNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}