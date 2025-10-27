package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.UserService;
import com.jobportal.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    @PostMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestParam String fullName,
                                                           @RequestParam String address,
                                                           @RequestParam String phone,
                                                           HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null || userId == 0) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFullName(fullName);
            user.setAddress(address);
            user.setPhone(phone);
            User updatedUser = userService.updateUser(user);
            
            if (updatedUser != null) {
                response.put("success", true);
                response.put("message", "Profile updated successfully");
                System.out.println("✅ Profile updated for user ID: " + userId);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update profile");
            }
        } else {
            response.put("success", false);
            response.put("message", "User not found");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user-applications")
    public ResponseEntity<?> getUserApplications(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Please login first"));
        }
        return ResponseEntity.ok(jobApplicationService.getUserApplications(userId));
    }
    
    @GetMapping("/user/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null || userId == 0) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Format the date properly
            String joinedDate = "Not available";
            if (user.getCreatedAt() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                joinedDate = dateFormat.format(java.sql.Timestamp.valueOf(user.getCreatedAt()));
            }
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("fullName", user.getFullName());
            userData.put("email", user.getEmail());
            userData.put("address", user.getAddress() != null ? user.getAddress() : "Not set");
            userData.put("phone", user.getPhone() != null ? user.getPhone() : "Not set");
            userData.put("profilePhotoPath", user.getProfilePhotoPath() != null ? user.getProfilePhotoPath() : "");
            userData.put("createdAt", joinedDate);
            userData.put("memberSince", joinedDate);
            
            response.put("success", true);
            response.put("data", userData);
        } else {
            response.put("success", false);
            response.put("message", "User not found");
        }
        return ResponseEntity.ok(response);
    }
}