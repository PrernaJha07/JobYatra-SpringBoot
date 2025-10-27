package com.jobportal.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "job_id", nullable = false)
    private Long jobId;
    
    @Column(name = "cv_file_id")
    private String cvFileId;
    
    private String status = "Pending";
    
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;
    
    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;
    
    public JobApplication() {
        this.appliedAt = LocalDateTime.now();
        this.statusUpdatedAt = LocalDateTime.now();
    }
    
    public JobApplication(Long userId, Long jobId, String cvFileId) {
        this();
        this.userId = userId;
        this.jobId = jobId;
        this.cvFileId = cvFileId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    
    public String getCvFileId() { return cvFileId; }
    public void setCvFileId(String cvFileId) { this.cvFileId = cvFileId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status; 
        this.statusUpdatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    
    public LocalDateTime getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) { this.statusUpdatedAt = statusUpdatedAt; }
}