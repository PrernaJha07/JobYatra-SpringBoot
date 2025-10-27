package com.jobportal.controller;

import com.jobportal.model.Feedback;
import com.jobportal.service.FeedbackService;
import com.jobportal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    private Long getUserIdFromSession(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
    
    private boolean isAdmin(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        return Boolean.TRUE.equals(isAdmin);
    }
    
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitFeedback(
            @RequestParam String feedbackText,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserIdFromSession(session);
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login to submit feedback");
            return ResponseEntity.ok(response);
        }
        
        try {
            String photoPath = null;
            if (photo != null && !photo.isEmpty()) {
                try {
                    photoPath = fileStorageService.storeFile(photo, userId, "feedback");
                    System.out.println("Photo stored at: " + photoPath);
                } catch (IOException e) {
                    System.err.println("Failed to store photo: " + e.getMessage());
                    // Continue without photo
                }
            }
            
            Feedback feedback = feedbackService.submitFeedback(userId, feedbackText, photoPath);
            response.put("success", true);
            response.put("message", "Feedback submitted successfully");
            response.put("data", feedback);
            
        } catch (Exception e) {
            System.err.println("Error in submitFeedback: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Failed to submit feedback: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserFeedback(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserIdFromSession(session);
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        try {
            var feedbackList = feedbackService.getUserFeedback(userId);
            response.put("success", true);
            response.put("data", feedbackList);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load feedback");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin/all")
    public ResponseEntity<Map<String, Object>> getAllFeedback(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            var feedbackList = feedbackService.getAllFeedback();
            response.put("success", true);
            response.put("data", feedbackList);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load feedback");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/admin/reply")
    public ResponseEntity<Map<String, Object>> replyToFeedback(
            @RequestParam Long feedbackId,
            @RequestParam String adminReply,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            Feedback feedback = feedbackService.replyToFeedback(feedbackId, adminReply);
            response.put("success", true);
            response.put("message", "Reply sent successfully");
            response.put("data", feedback);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send reply: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/admin/delete")
    public ResponseEntity<Map<String, Object>> deleteFeedback(
            @RequestParam Long feedbackId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.ok(response);
        }
        
        try {
            feedbackService.deleteFeedback(feedbackId);
            response.put("success", true);
            response.put("message", "Feedback deleted successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete feedback: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> checkNotifications(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserIdFromSession(session);
        
        if (userId == null) {
            response.put("success", false);
            response.put("hasNewReplies", false);
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean hasNewReplies = feedbackService.hasNewReplies(userId);
            response.put("success", true);
            response.put("hasNewReplies", hasNewReplies);
        } catch (Exception e) {
            response.put("success", false);
            response.put("hasNewReplies", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, Object>> markAsRead(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserIdFromSession(session);
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        try {
            feedbackService.markFeedbackAsRead(userId);
            response.put("success", true);
            response.put("message", "Marked as read");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to mark as read");
        }
        
        return ResponseEntity.ok(response);
    }
}