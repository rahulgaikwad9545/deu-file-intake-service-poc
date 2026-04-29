package com.deupoc.fileintakeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the File Intake Service.
 * This is a Spring Boot application that provides REST API endpoints
 * for importing (uploading) and exporting (downloading) files to/from AWS S3 storage.
 *
 * Features:
 * - File upload (import) with metadata storage
 * - File download (export) with proper headers
 * - File metadata management
 * - File listing and deletion
 * - OpenAPI/Swagger documentation
 * - CORS support for frontend integration
 * - H2 database for metadata storage
 */
@SpringBootApplication
public class FileintakeserviceApplication {

	/**
	 * Main method that starts the Spring Boot application.
	 * This method bootstraps the application context and starts the embedded server.
	 *
	 * @param args command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(FileintakeserviceApplication.class, args);
	}

}
