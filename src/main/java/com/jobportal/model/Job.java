package com.jobportal.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String company;
    private String location;
    private String salary;
    
    @Column(name = "job_type")
    private String jobType;
    
    @Column(name = "company_logo_path")
    private String companyLogoPath;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    private boolean active = true;
    
    public Job() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Job(String title, String description, String company, String location, String salary, String jobType) {
        this();
        this.title = title;
        this.description = description;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    
    public String getCompanyLogoPath() { return companyLogoPath; }
    public void setCompanyLogoPath(String companyLogoPath) { this.companyLogoPath = companyLogoPath; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}