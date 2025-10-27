package com.jobportal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "files")
public class FileDocument {
    @Id
    private String id;
    
    private String filename;
    private String fileType;
    private Long fileSize;
    private byte[] data;
    private Long userId;
    private String fileCategory; // profile_photo, cv, company_logo
    private LocalDateTime uploadedAt;
    private String localFilePath; // Path in local filesystem
    
    // Constructors
    public FileDocument() {
        this.uploadedAt = LocalDateTime.now();
    }
    
    public FileDocument(String filename, String fileType, Long fileSize, byte[] data, 
                       Long userId, String fileCategory) {
        this();
        this.filename = filename;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.data = data;
        this.userId = userId;
        this.fileCategory = fileCategory;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getFileCategory() { return fileCategory; }
    public void setFileCategory(String fileCategory) { this.fileCategory = fileCategory; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public String getLocalFilePath() { return localFilePath; }
    public void setLocalFilePath(String localFilePath) { this.localFilePath = localFilePath; }
}