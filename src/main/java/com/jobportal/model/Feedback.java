package com.jobportal.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks") // Changed table name to avoid conflicts
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "user_email", length = 255)
    private String userEmail;
    
    @Column(name = "feedback_text", columnDefinition = "TEXT", nullable = false)
    private String feedbackText;
    
    @Column(name = "photo_path", length = 500)
    private String photoPath;
    
    @Column(name = "status", length = 20)
    private String status = "pending";
    
    @Column(name = "admin_reply", columnDefinition = "TEXT")
    private String adminReply;
    
    @Column(name = "read_status")
    private Boolean readStatus = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "replied_at")
    private LocalDateTime repliedAt;
    
    // Constructors
    public Feedback() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Feedback(Long userId, String userEmail, String feedbackText) {
        this();
        this.userId = userId;
        this.userEmail = userEmail;
        this.feedbackText = feedbackText;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
    
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { 
        this.adminReply = adminReply;
        if (adminReply != null && !adminReply.trim().isEmpty()) {
            this.repliedAt = LocalDateTime.now();
            this.status = "replied";
        }
    }
    
    public Boolean getReadStatus() { return readStatus; }
    public void setReadStatus(Boolean readStatus) { this.readStatus = readStatus; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }
}