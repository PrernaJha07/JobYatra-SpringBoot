package com.jobportal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "video_profiles")
public class VideoProfile {
    @Id
    private String id;
    
    private Long userId;
    private String userEmail;
    private String userName;
    private String videoFileId;
    private String videoUrl;
    private String thumbnailUrl;
    private Long fileSize;
    private String duration;
    private LocalDateTime uploadedAt;
    private LocalDateTime expiresAt;
    private boolean active = true;
    
    public VideoProfile() {
        this.uploadedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(5);
    }
    
    public VideoProfile(Long userId, String userEmail, String userName, String videoFileId, String videoUrl, Long fileSize, String duration) {
        this();
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.videoFileId = videoFileId;
        this.videoUrl = videoUrl;
        this.fileSize = fileSize;
        this.duration = duration;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getVideoFileId() { return videoFileId; }
    public void setVideoFileId(String videoFileId) { this.videoFileId = videoFileId; }
    
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}