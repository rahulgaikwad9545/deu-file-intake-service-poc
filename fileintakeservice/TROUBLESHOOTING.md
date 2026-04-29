# File Intake Service - Troubleshooting Guide

## Issue: "File operation failed: Failed to upload clob file: employee_contact.json"

This error indicates that the file upload operation to AWS S3 failed. Here are the possible causes and solutions:

## Common Causes & Solutions

### 1. **Invalid AWS Credentials** ⚠️ [MOST LIKELY]
**Problem**: The credentials in `application.properties` are placeholders.

**Current Configuration**:
```properties
aws.access-key-id=[REDACTED_AWS_CREDENTIALS_1]
aws.secret-access-key=[REDACTED_AWS_SECRET_KEY_CREDENTIALS_1]
```

**Solution**:
- Replace with actual AWS Access Key ID and Secret Access Key
- Get credentials from AWS IAM Console
- Store securely (don't commit to version control)

**Alternative**: Use environment variables instead:
```bash
export AWS_ACCESS_KEY_ID=your-actual-access-key
export AWS_SECRET_ACCESS_KEY=your-actual-secret-key
```

### 2. **S3 Bucket Doesn't Exist or Inaccessible**
**Problem**: The bucket `sqs-postgresql-bucket` may not exist or user doesn't have access.

**Solution**:
- Verify bucket exists in AWS S3 Console
- Check IAM permissions include `s3:PutObject` and `s3:GetObject`
- Verify bucket name is correct

### 3. **Database Configuration Missing**
**Problem**: Database configuration was not properly initialized.

**Status**: ✅ FIXED - Database configuration added to `application.properties`
```properties
spring.datasource.url=jdbc:h2:mem:fileintake
spring.jpa.hibernate.ddl-auto=create-drop
```

### 4. **Repository Not Injected**
**Problem**: FileMetadataRepository might not be properly injected into S3Service.

**Status**: ✅ FIXED - Constructor injection implemented

## Debugging Steps

### Step 1: Check the Logs
Enable DEBUG logging to see detailed error messages:

```properties
logging.level.com.deupoc.fileintakeservice=DEBUG
logging.level.org.springframework.web=INFO
logging.level.software.amazon.awssdk=DEBUG
```

Look for error messages that indicate:
- S3 authentication failure
- Bucket not found
- Permission denied
- Network connectivity issues

### Step 2: Verify AWS Configuration
Check if S3Service is initialized properly by looking for startup logs:
```
S3Service initialized with bucket: sqs-postgresql-bucket
```

If you see:
```
WARNING: aws.s3.bucket-name property not configured!
```
This means the property is not being read correctly.

### Step 3: Test AWS Credentials
Create a simple test endpoint to verify AWS connectivity:

```java
@GetMapping("/test-s3-connection")
public ResponseEntity<Map<String, String>> testS3Connection() {
    try {
        s3Client.headBucket(HeadBucketRequest.builder()
                .bucket(bucketName)
                .build());
        return ResponseEntity.ok(Map.of("status", "Connected successfully"));
    } catch (Exception e) {
        return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
    }
}
```

### Step 4: Check Database Tables
Access H2 Console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:fileintake`
- Username: `sa`
- Password: (empty)

Verify table `FILE_METADATA` exists:
```sql
SELECT * FROM FILE_METADATA;
```

## Enhanced Error Messages

The improved error handling now provides:
- **Underlying Exception Type**: What actually failed (e.g., SdkClientException, DatabaseException)
- **Detailed Messages**: Full error description
- **Stack Traces**: In DEBUG logs for complete diagnosis

Example improved response:
```json
{
  "error": "File operation failed",
  "message": "Failed to upload clob file: employee_contact.json - SdkClientException: Invalid AWS credentials",
  "cause": "SdkClientException"
}
```

## Configuration Checklist

- [ ] AWS Access Key ID is set (not placeholder)
- [ ] AWS Secret Access Key is set (not placeholder)
- [ ] AWS Region is valid (us-east-1, etc.)
- [ ] S3 Bucket exists and is accessible
- [ ] User has S3 permissions: `s3:PutObject`, `s3:GetObject`
- [ ] Database configuration is in application.properties
- [ ] Logging level set to DEBUG for troubleshooting
- [ ] H2 console is enabled to verify database tables

## Quick Fix - Development Testing

For local testing without AWS credentials, you can:

1. **Use LocalStack** (Docker): Simulate AWS S3 locally
   ```bash
   docker run -p 4566:4566 localstack/localstack
   ```

2. **Use S3Mock Library**: Unit test with mock S3

3. **Skip S3 for Now**: Modify code to save to disk temporarily for testing

## Error Log Analysis

### If you see authentication error:
```
InvalidSignatureException: Signature does not match
```
→ Check AWS credentials are correct

### If you see bucket not found:
```
NoSuchBucket: The specified bucket does not exist
```
→ Verify bucket name and create if needed

### If you see permission error:
```
AccessDenied: Access Denied
```
→ Check IAM policy allows S3 operations

### If you see database error:
```
HibernateException or PersistenceException
```
→ Check database is initialized and table exists

## Next Steps

1. Update AWS credentials in `application.properties`
2. Run application with DEBUG logging enabled
3. Check console logs for specific error messages
4. Use steps above to diagnose the exact issue
5. Once fixed, test with the import API

---

**For more help**: Check the full error message in the API response body - it now includes the root cause exception type and detailed message.
