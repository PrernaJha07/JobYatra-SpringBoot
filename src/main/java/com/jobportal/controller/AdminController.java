package com.jobportal.controller;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.model.JobApplication;
import com.jobportal.service.AdminService;
import com.jobportal.service.UserService;
import com.jobportal.service.JobService;
import com.jobportal.service.JobApplicationService;
import com.jobportal.service.FileStorageService;
import com.jobportal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private EmailService emailService;
    
    private boolean isAdmin(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        boolean isAdminUser = userId != null && userId == 0L && Boolean.TRUE.equals(isAdmin);
        System.out.println("🔐 Admin check - UserID: " + userId + ", isAdmin: " + isAdmin + ", Result: " + isAdminUser);
        return isAdminUser;
    }
    
    // Dashboard Statistics
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> adminDashboard(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("📊 Admin dashboard requested");
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            System.out.println("❌ Admin access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            Map<String, Object> stats = adminService.getDashboardStats();
            response.put("success", true);
            response.put("data", stats);
            System.out.println("✅ Dashboard data sent successfully: " + stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load dashboard: " + e.getMessage());
            System.err.println("❌ Dashboard error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(response);
        }
    }
    
    // Get All Users
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("👥 Admin users list requested");
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            List<User> users = userService.getAllUsers();
            response.put("success", true);
            response.put("data", users);
            System.out.println("✅ Sent " + users.size() + " users to admin");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load users: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // Get All Applications
    @GetMapping("/applications")
    public ResponseEntity<Map<String, Object>> getAllApplications(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("📋 Admin applications list requested");
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            List<JobApplication> applications = jobApplicationService.getAllApplications();
            response.put("success", true);
            response.put("data", applications);
            System.out.println("✅ Sent " + applications.size() + " applications to admin");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load applications: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // Update User Email
    @PostMapping("/update-email")
    public ResponseEntity<Map<String, Object>> updateUserEmail(@RequestParam Long userId,
                                                             @RequestParam String newEmail,
                                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("📧 Update email requested for user: " + userId + " to: " + newEmail);
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String oldEmail = user.getEmail();
                user.setEmail(newEmail);
                User updatedUser = userService.updateUser(user);
                
                if (updatedUser != null) {
                    response.put("success", true);
                    response.put("message", "Email updated successfully");
                    System.out.println("✅ Email updated for user ID: " + userId + " from " + oldEmail + " to " + newEmail);
                } else {
                    response.put("success", false);
                    response.put("message", "Failed to update email");
                }
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update email: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    // Update Application Status with Email Notification
    @PostMapping("/update-application-status")
    public ResponseEntity<Map<String, Object>> updateApplicationStatus(@RequestParam Long applicationId,
                                                                     @RequestParam String status,
                                                                     HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("🔄 Update application status: " + applicationId + " to " + status);
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            Optional<JobApplication> applicationOpt = jobApplicationService.getApplicationById(applicationId);
            if (applicationOpt.isPresent()) {
                JobApplication application = applicationOpt.get();
                String oldStatus = application.getStatus();
                application.setStatus(status);
                JobApplication updatedApplication = jobApplicationService.updateApplication(application);
                
                if (updatedApplication != null) {
                    // Send email notification
                    boolean emailSent = sendStatusEmail(application, status);
                    
                    response.put("success", true);
                    response.put("message", "Status updated successfully");
                    response.put("emailSent", emailSent);
                    response.put("notification", "User " + application.getUserId() + " application status changed from " + oldStatus + " to " + status);
                    System.out.println("✅ Application status updated for ID: " + applicationId + " from " + oldStatus + " to " + status);
                    System.out.println("📧 Email sent: " + emailSent);
                } else {
                    response.put("success", false);
                    response.put("message", "Failed to update status");
                }
            } else {
                response.put("success", false);
                response.put("message", "Application not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update status: " + e.getMessage());
            System.err.println("❌ Error updating application status: " + e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(response);
    }
    
    private boolean sendStatusEmail(JobApplication application, String status) {
        try {
            Optional<User> userOpt = userService.getUserById(application.getUserId());
            Optional<Job> jobOpt = jobService.getJobById(application.getJobId());
            
            if (userOpt.isPresent() && jobOpt.isPresent()) {
                User user = userOpt.get();
                Job job = jobOpt.get();
                
                return emailService.sendApplicationStatusEmail(
                    user.getEmail(),
                    user.getFullName(),
                    job.getTitle(),
                    job.getCompany(),
                    status
                );
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Email error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Add New Job - COMPLETELY FIXED
    @PostMapping("/add-job")
    public ResponseEntity<Map<String, Object>> addJob(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("company") String company,
            @RequestParam("location") String location,
            @RequestParam("salary") String salary,
            @RequestParam("jobType") String jobType,
            @RequestParam(value = "companyLogo", required = false) MultipartFile companyLogo,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        System.out.println("🆕 Add job request received");
        System.out.println("📝 Job Details:");
        System.out.println("   Title: " + title);
        System.out.println("   Company: " + company);
        System.out.println("   Location: " + location);
        System.out.println("   Salary: " + salary);
        System.out.println("   Type: " + jobType);
        System.out.println("   Logo: " + (companyLogo != null ? companyLogo.getOriginalFilename() : "No logo"));
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            System.out.println("❌ Admin access denied for job creation");
            return ResponseEntity.ok(response);
        }
        
        // Validate required fields
        if (title == null || title.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Job title is required");
            return ResponseEntity.ok(response);
        }
        
        if (company == null || company.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Company name is required");
            return ResponseEntity.ok(response);
        }
        
        try {
            // Create new job object
            Job job = new Job();
            job.setTitle(title.trim());
            job.setDescription(description != null ? description.trim() : "");
            job.setCompany(company.trim());
            job.setLocation(location != null ? location.trim() : "");
            job.setSalary(salary != null ? salary.trim() : "");
            job.setJobType(jobType != null ? jobType.trim() : "Full-time");
            job.setActive(true);
            
            System.out.println("💾 Creating job object: " + job.getTitle());
            
            // Handle company logo if provided
            if (companyLogo != null && !companyLogo.isEmpty()) {
                System.out.println("🖼️ Processing company logo...");
                
                // Validate file type
                if (!fileStorageService.validateImageFile(companyLogo)) {
                    response.put("success", false);
                    response.put("message", "Invalid file type. Please upload an image file.");
                    return ResponseEntity.ok(response);
                }
                
                try {
                    String filePath = fileStorageService.storeFile(companyLogo, 0L, "company_logo");
                    job.setCompanyLogoPath(filePath);
                    System.out.println("✅ Company logo stored at: " + filePath);
                } catch (Exception e) {
                    System.err.println("❌ Failed to store company logo: " + e.getMessage());
                    // Continue without logo - don't fail the entire job creation
                    job.setCompanyLogoPath("");
                }
            } else {
                System.out.println("ℹ️ No company logo provided");
                job.setCompanyLogoPath("");
            }
            
            // Save job to database
            System.out.println("💾 Saving job to database...");
            Job savedJob = jobService.createJob(job);
            
            if (savedJob != null && savedJob.getId() != null) {
                response.put("success", true);
                response.put("message", "Job added successfully");
                response.put("jobId", savedJob.getId());
                response.put("job", savedJob);
                System.out.println("✅ Job saved successfully with ID: " + savedJob.getId());
                System.out.println("✅ Job Title: " + savedJob.getTitle());
                System.out.println("✅ Job Company: " + savedJob.getCompany());
            } else {
                response.put("success", false);
                response.put("message", "Failed to save job to database");
                System.err.println("❌ Job save returned null or no ID");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add job: " + e.getMessage());
            System.err.println("❌ Job creation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Get All Jobs (Admin View)
    @GetMapping("/jobs")
    public ResponseEntity<Map<String, Object>> getAllJobs(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("📋 Admin jobs list requested");
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            List<Job> jobs = jobService.getAllJobs();
            response.put("success", true);
            response.put("data", jobs);
            System.out.println("✅ Sent " + jobs.size() + " jobs to admin");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load jobs: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // Update Job
    @PostMapping("/update-job")
    public ResponseEntity<Map<String, Object>> updateJob(@RequestParam Long jobId,
                                                       @RequestParam String title,
                                                       @RequestParam String description,
                                                       @RequestParam String company,
                                                       @RequestParam String location,
                                                       @RequestParam String salary,
                                                       @RequestParam String jobType,
                                                       @RequestParam boolean active,
                                                       HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("✏️ Update job request: " + jobId);
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            Optional<Job> jobOpt = jobService.getJobById(jobId);
            if (jobOpt.isPresent()) {
                Job job = jobOpt.get();
                job.setTitle(title);
                job.setDescription(description);
                job.setCompany(company);
                job.setLocation(location);
                job.setSalary(salary);
                job.setJobType(jobType);
                job.setActive(active);
                
                Job updatedJob = jobService.createJob(job);
                if (updatedJob != null) {
                    response.put("success", true);
                    response.put("message", "Job updated successfully");
                    System.out.println("✅ Job updated with ID: " + jobId);
                } else {
                    response.put("success", false);
                    response.put("message", "Failed to update job");
                }
            } else {
                response.put("success", false);
                response.put("message", "Job not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update job: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    // Delete Job
    @PostMapping("/delete-job")
    public ResponseEntity<Map<String, Object>> deleteJob(@RequestParam Long jobId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("🗑️ Delete job request: " + jobId);
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean deleted = jobService.deleteJob(jobId);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Job deleted successfully");
                System.out.println("✅ Job soft deleted with ID: " + jobId);
            } else {
                response.put("success", false);
                response.put("message", "Job not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete job: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}