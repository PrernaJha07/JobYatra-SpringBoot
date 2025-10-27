package com.jobportal.controller;

import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.service.JobService;
import com.jobportal.service.JobApplicationService;
import com.jobportal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping("/jobs")
    public ResponseEntity<Map<String, Object>> getAllJobs() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Job> jobs = jobService.getAllActiveJobs();
            
            // Ensure all jobs have proper company names
            for (Job job : jobs) {
                if (job.getCompany() == null || job.getCompany().trim().isEmpty()) {
                    job.setCompany("Tech Company");
                }
                if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
                    job.setTitle("Software Developer");
                }
            }
            
            response.put("success", true);
            response.put("data", jobs);
            System.out.println("✅ Returning " + jobs.size() + " jobs with proper company names");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load jobs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/apply-job")
    public ResponseEntity<Map<String, Object>> applyJob(@RequestParam Long jobId,
                                                      @RequestParam("cv") MultipartFile cvFile,
                                                      HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login to apply for jobs");
            return ResponseEntity.ok(response);
        }
        
        // Validate job exists
        var jobOpt = jobService.getJobById(jobId);
        if (jobOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Job not found");
            return ResponseEntity.ok(response);
        }
        
        if (jobApplicationService.hasUserAppliedForJob(userId, jobId)) {
            response.put("success", false);
            response.put("message", "You have already applied for this job");
            return ResponseEntity.ok(response);
        }
        
        try {
            String filePath = fileStorageService.storeFile(cvFile, userId, "cv");
            
            JobApplication application = new JobApplication(userId, jobId, filePath);
            JobApplication savedApplication = jobApplicationService.applyForJob(application);
            
            if (savedApplication != null && savedApplication.getId() != null) {
                response.put("success", true);
                response.put("message", "Application submitted successfully!");
                System.out.println("✅ Application saved with ID: " + savedApplication.getId() + " for job: " + jobId);
            } else {
                response.put("success", false);
                response.put("message", "Failed to save application");
            }
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload CV: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Application failed: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/jobs/search")
    public ResponseEntity<Map<String, Object>> searchJobs(@RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Job> jobs = jobService.searchJobs(query);
            
            // Ensure proper company names in search results
            for (Job job : jobs) {
                if (job.getCompany() == null || job.getCompany().trim().isEmpty()) {
                    job.setCompany("Tech Company");
                }
            }
            
            response.put("success", true);
            response.put("data", jobs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Search failed: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}