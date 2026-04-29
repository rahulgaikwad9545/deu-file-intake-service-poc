package com.deupoc.fileintakeservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a file stored in the system.
 * Contains both blob (binary large object) and clob (character large object) storage capabilities.
 */
@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {

    /**
     * Unique identifier for the file
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Original filename
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * MIME type of the file
     */
    @Column(nullable = false)
    private String contentType;

    /**
     * Size of the file in bytes
     */
    @Column(nullable = false)
    private Long fileSize;

    /**
     * Binary data for blob storage (for binary files like images, documents, etc.)
     */
    @Lob
    @Column(name = "file_data_blob")
    private byte[] fileDataBlob;

    /**
     * Character data for clob storage (for text files, JSON, XML, etc.)
     */
    @Lob
    @Column(name = "file_data_clob")
    private String fileDataClob;

    /**
     * S3 key for files stored in AWS S3
     */
    @Column(name = "s3_key")
    private String s3Key;

    /**
     * Timestamp when the file was uploaded
     */
    @Column(nullable = false)
    private LocalDateTime uploadTime;

    /**
     * Status of the file processing
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    /**
     * Additional metadata in JSON format
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    public enum FileStatus {
        UPLOADED,
        PROCESSED,
        PARSED,
        ERROR
    }
}