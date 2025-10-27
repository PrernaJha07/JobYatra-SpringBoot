package com.jobportal.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_replies")
public class ReviewReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "review_id", nullable = false)
    private Long reviewId;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "user_photo")
    private String userPhoto;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_auto_reply")
    private Boolean isAutoReply = false;
    
    public ReviewReply() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ReviewReply(Long reviewId, String userName, String userPhoto, String text, Boolean isAutoReply) {
        this();
        this.reviewId = reviewId;
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.text = text;
        this.isAutoReply = isAutoReply;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserPhoto() { return userPhoto; }
    public void setUserPhoto(String userPhoto) { this.userPhoto = userPhoto; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Boolean getIsAutoReply() { return isAutoReply; }
    public void setIsAutoReply(Boolean autoReply) { isAutoReply = autoReply; }
}