package com.jobportal.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "job_id", nullable = false)
    private Long jobId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "user_photo")
    private String userPhoto;
    
    @Column(nullable = false)
    private Integer rating;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private List<ReviewReply> replies = new ArrayList<>();
    
    public Review() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Review(Long jobId, Long userId, String userName, String userPhoto, Integer rating, String text) {
        this();
        this.jobId = jobId;
        this.userId = userId;
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.rating = rating;
        this.text = text;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserPhoto() { return userPhoto; }
    public void setUserPhoto(String userPhoto) { this.userPhoto = userPhoto; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<ReviewReply> getReplies() { return replies; }
    public void setReplies(List<ReviewReply> replies) { this.replies = replies; }
    
    public void addReply(ReviewReply reply) {
        this.replies.add(reply);
    }
}