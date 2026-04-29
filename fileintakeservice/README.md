# File Intake Service

A Spring Boot application that provides REST API endpoints for importing (uploading) and exporting (downloading) files to/from AWS S3 storage with metadata management.

## Features

- **File Import**: Upload files to AWS S3 with automatic metadata storage
- **File Export**: Download files from S3 with proper content headers
- **Metadata Management**: Track file information in H2 database
- **File Operations**: List, view metadata, and delete files
- **OpenAPI Documentation**: Interactive API documentation via Swagger UI
- **CORS Support**: Configured for frontend integration
- **Exception Handling**: Comprehensive error handling with custom exceptions

## Technology Stack

- **Java 17**
- **Spring Boot 4.0.6**
- **AWS SDK v2** for S3 operations
- **Spring Data JPA** for database operations
- **H2 Database** for metadata storage
- **SpringDoc OpenAPI** for API documentation
- **Lombok** for reducing boilerplate code

## API Endpoints

### File Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/files/import` | Import (upload) a file |
| GET | `/api/files/export/{fileName}` | Export (download) a file |
| GET | `/api/files/metadata/{fileName}` | Get file metadata |
| GET | `/api/files/list` | List all files |
| DELETE | `/api/files/{fileName}` | Delete a file |

### Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Configuration

### Application Properties

```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:fileintake
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.h2.console.enabled=true

# AWS S3 Configuration
aws.s3.bucket-name=your-bucket-name
aws.access-key-id=your-access-key
aws.secret-access-key=your-secret-key
aws.region=us-east-1
```

### Environment Variables

Set the following environment variables for AWS credentials:

```bash
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 7.0+ or Maven 3.6+

### Running the Application

1. Clone the repository
2. Configure AWS credentials in `application.properties`
3. Run with Gradle:
   ```bash
   ./gradlew bootRun
   ```
4. Or with Maven:
   ```bash
   mvn spring-boot:run
   ```

### Building the Application

```bash
./gradlew clean build
```

### Testing

```bash
./gradlew test
```

## Database

The application uses H2 in-memory database for storing file metadata. The H2 console is available at `http://localhost:8080/h2-console` when `spring.h2.console.enabled=true`.

## File Storage Types

- **Blob**: Binary files (images, documents, etc.)
- **Clob**: Text files (JSON, XML, CSV, plain text)

Files are automatically categorized based on their content type.

## Error Handling

The application includes comprehensive error handling:

- `FileNotFoundException`: When requested file doesn't exist
- `FileOperationException`: For S3 operation failures
- `IllegalArgumentException`: For invalid input parameters
- Global exception handler for consistent error responses

## CORS Configuration

CORS is configured to allow requests from common frontend development ports:
- `http://localhost:3000` (React)
- `http://localhost:4200` (Angular)
- `http://localhost:5173` (Vite)

## Security Considerations

- AWS credentials should be stored securely (environment variables, AWS IAM roles)
- In production, restrict CORS origins to your domain
- Consider implementing authentication/authorization
- Add file type and size validation as needed

## Contributing

1. Follow Java coding standards
2. Add comprehensive comments and documentation
3. Write unit tests for new features
4. Update API documentation for new endpoints

## License

This project is licensed under the MIT License.