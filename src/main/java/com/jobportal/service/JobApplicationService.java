package com.jobportal.service;

import com.jobportal.model.JobApplication;
import com.jobportal.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobApplicationService {
    
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    public JobApplication applyForJob(JobApplication application) {
        return jobApplicationRepository.save(application);
    }
    
    public List<JobApplication> getUserApplications(Long userId) {
        return jobApplicationRepository.findByUserId(userId);
    }
    
    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAllByOrderByAppliedAtDesc();
    }
    
    public Optional<JobApplication> getApplicationById(Long id) {
        return jobApplicationRepository.findById(id);
    }
    
    public JobApplication updateApplication(JobApplication application) {
        return jobApplicationRepository.save(application);
    }
    
    public boolean hasUserAppliedForJob(Long userId, Long jobId) {
        return jobApplicationRepository.findByUserIdAndJobId(userId, jobId).isPresent();
    }
    
    public long getTotalApplications() {
        return jobApplicationRepository.count();
    }
    
    public long getApplicationsByStatus(String status) {
        return jobApplicationRepository.countByStatus(status);
    }
 // Add this method to JobApplicationService.java
    public List<Map<String, Object>> getAllApplicationsWithDetails() {
        List<JobApplication> applications = jobApplicationRepository.findAllByOrderByAppliedAtDesc();
        List<Map<String, Object>> applicationData = new ArrayList<>();
        
        for (JobApplication app : applications) {
            Map<String, Object> data = new HashMap<>();
            data.put("application", app);
            
            // You can add user and job details here if needed
            // This would require injecting UserService and JobService
            
            applicationData.add(data);
        }
        
        return applicationData;
    }
}