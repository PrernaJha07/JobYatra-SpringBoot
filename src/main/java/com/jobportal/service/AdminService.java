package com.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    @Value("${admin.email}")
    private String adminEmail;
    
    @Value("${admin.password}")
    private String adminPassword;
    
    public boolean isAdminUser(String email, String password) {
        return adminEmail.equals(email) && adminPassword.equals(password);
    }
    
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalUsers", userService.getTotalUsers());
            stats.put("totalJobs", jobService.getTotalJobs());
            stats.put("totalApplications", jobApplicationService.getTotalApplications());
            stats.put("pendingApplications", jobApplicationService.getApplicationsByStatus("Pending"));
            stats.put("successApplications", jobApplicationService.getApplicationsByStatus("Success"));
            stats.put("rejectedApplications", jobApplicationService.getApplicationsByStatus("Rejected"));
            System.out.println("📊 Dashboard Stats: " + stats);
        } catch (Exception e) {
            System.err.println("❌ Error getting dashboard stats: " + e.getMessage());
            // Set default values
            stats.put("totalUsers", 0);
            stats.put("totalJobs", 0);
            stats.put("totalApplications", 0);
            stats.put("pendingApplications", 0);
            stats.put("successApplications", 0);
            stats.put("rejectedApplications", 0);
        }
        return stats;
    }
}