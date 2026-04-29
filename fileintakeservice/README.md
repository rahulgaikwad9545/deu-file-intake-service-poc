# File Intake Service

A comprehensive Spring Boot application for file intake, processing, and management with support for blob and CLOB storage, text-to-JSON parsing, and various export formats.

## Features

### Core Functionality
- **File Upload**: Upload files with options to store in database (blob/clob) and/or AWS S3
- **File Download**: Download files from S3 or database storage
- **Text Parsing**: Parse text files (CSV, TSV, custom formats) to JSON
- **File Management**: CRUD operations for file metadata
- **Export/Import**: Export files in JSON, XML, or CSV formats

### Storage Capabilities
- **Blob Storage**: Binary large object storage for files like images, documents, etc.
- **CLOB Storage**: Character large object storage for text files
- **AWS S3 Integration**: Cloud storage for scalable file management
- **Database Storage**: H2 in-memory database for development (easily configurable for production)

### Parsing & Processing
- **Multi-format Support**: CSV, TSV, pipe-delimited, semicolon-delimited, and custom formats
- **Header Detection**: Automatic header detection for structured data
- **JSON Conversion**: Convert parsed data to structured JSON format
- **Validation**: JSON validation and field extraction utilities

## Technology Stack

- **Framework**: Spring Boot 4.0.6
- **Database**: H2 (in-memory) with JPA/Hibernate
- **Cloud Storage**: AWS S3
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Gradle
- **Language**: Java 17

## Prerequisites

- Java 17 or higher
- Gradle 7.x or higher
- AWS Account with S3 bucket (for cloud storage)

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
aws.s3.bucket-name=your-s3-bucket-name
aws.access-key-id=your-aws-access-key
aws.secret-access-key=your-aws-secret-key
aws.region=us-east-1
```

### AWS S3 Setup

1. Create an S3 bucket in your AWS account
2. Create an IAM user with S3 permissions or use existing credentials
3. Update the `application.properties` with your bucket name and credentials

## API Endpoints

### File Operations

#### Upload File
```http
POST /api/files/upload
Content-Type: multipart/form-data

Parameters:
- file: MultipartFile (required)
- storeInDatabase: boolean (default: true)
- storeInS3: boolean (default: true)
```

#### Parse Text File to JSON
```http
POST /api/files/{fileId}/parse
Parameters:
- format: String (CSV, TSV, PIPE, SEMICOLON, CUSTOM) (default: CSV)
- hasHeaders: boolean (default: true)
```

#### Download File
```http
GET /api/files/{fileId}/download
```

#### Export File
```http
GET /api/files/{fileId}/export?format={JSON|XML|CSV}
```

#### Get File Metadata
```http
GET /api/files/{fileId}/metadata
```

#### Get All Files
```http
GET /api/files
```

#### Get Files by Status
```http
GET /api/files/status/{status}
```

#### Get Files with Blob Data
```http
GET /api/files/blob
```

#### Get Files with CLOB Data
```http
GET /api/files/clob
```

#### Delete File
```http
DELETE /api/files/{fileId}
```

## Usage Examples

### 1. Upload a CSV File and Parse to JSON

```bash
# Upload file
curl -X POST -F "file=@data.csv" -F "storeInDatabase=true" -F "storeInS3=true" \
  http://localhost:8080/api/files/upload

# Parse to JSON (assuming file ID is 1)
curl -X POST "http://localhost:8080/api/files/1/parse?format=CSV&hasHeaders=true"
```

### 2. Download Processed File

```bash
curl -O -J http://localhost:8080/api/files/1/download
```

### 3. Export as JSON

```bash
curl -O -J "http://localhost:8080/api/files/1/export?format=JSON"
```

## File Processing Workflow

1. **Upload**: File is uploaded and stored in database (blob/clob) and/or S3
2. **Parse**: Text files can be parsed to JSON format with metadata storage
3. **Process**: Files can be processed and their status updated
4. **Export**: Processed data can be exported in various formats
5. **Download**: Files can be downloaded from storage

## Exception Handling

The application includes comprehensive exception handling:

- **File Upload Errors**: Invalid files, size limits, storage failures
- **Parsing Errors**: Invalid formats, malformed data, encoding issues
- **Storage Errors**: Database connection issues, S3 connectivity problems
- **Validation Errors**: Missing required fields, invalid parameters

All exceptions are logged with appropriate error messages and HTTP status codes.

## Database Schema

### Files Table

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| file_name | VARCHAR | Original filename |
| content_type | VARCHAR | MIME type |
| file_size | BIGINT | Size in bytes |
| file_data_blob | BLOB | Binary data |
| file_data_clob | CLOB | Text data |
| s3_key | VARCHAR | S3 object key |
| upload_time | TIMESTAMP | Upload timestamp |
| status | VARCHAR | Processing status |
| metadata | TEXT | Additional JSON metadata |

## Development

### Building the Application

```bash
./gradlew build
```

### Running the Application

```bash
./gradlew bootRun
```

### Running Tests

```bash
./gradlew test
```

### H2 Console

When running in development mode, access the H2 console at:
```
http://localhost:8080/h2-console
```

Use the following connection details:
- JDBC URL: `jdbc:h2:mem:fileintake`
- Username: `sa`
- Password: (empty)

### API Documentation

Access Swagger UI documentation at:
```
http://localhost:8080/swagger-ui.html
```

## Production Considerations

### Database
- Replace H2 with production database (PostgreSQL, MySQL, etc.)
- Configure connection pooling
- Enable database migrations

### Security
- Implement authentication/authorization
- Use IAM roles instead of access keys for AWS
- Add file type validation and virus scanning
- Implement rate limiting

### Storage
- Configure S3 bucket policies and lifecycle rules
- Implement file versioning
- Add backup and disaster recovery

### Monitoring
- Add application metrics
- Implement health checks
- Configure logging and alerting

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the Apache 2.0 License - see the LICENSE file for details.