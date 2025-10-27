package com.jobportal.controller;

import com.jobportal.model.Review;
import com.jobportal.model.ReviewReply;
import com.jobportal.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addReview(@RequestBody Review review, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login to add a review");
            return ResponseEntity.ok(response);
        }
        
        try {
            review.setUserId(userId);
            Review savedReview = reviewService.addReview(review);
            
            response.put("success", true);
            response.put("message", "Thank you for your review!");
            response.put("review", savedReview);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/job/{jobId}")
    public ResponseEntity<Map<String, Object>> getJobReviews(@PathVariable Long jobId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> reviews = reviewService.getReviewsByJobId(jobId);
            Map<String, Object> summary = reviewService.getJobRatingSummary(jobId);
            
            response.put("success", true);
            response.put("reviews", reviews);
            response.put("summary", summary);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load reviews");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/job/{jobId}/summary")
    public ResponseEntity<Map<String, Object>> getJobRatingSummary(@PathVariable Long jobId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> summary = reviewService.getJobRatingSummary(jobId);
            response.put("success", true);
            response.put("summary", summary);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load rating summary");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all-summaries")
    public ResponseEntity<Map<String, Object>> getAllJobsRatingSummaries() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<Long, Map<String, Object>> summaries = reviewService.getAllJobsRatingSummary();
            response.put("success", true);
            response.put("summaries", summaries);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load rating summaries");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<Map<String, Object>> addReply(@PathVariable Long reviewId, 
                                                      @RequestBody ReviewReply reply,
                                                      HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login to add a reply");
            return ResponseEntity.ok(response);
        }
        
        try {
            ReviewReply savedReply = reviewService.addReplyToReview(reviewId, reply);
            response.put("success", true);
            response.put("message", "Reply added successfully");
            response.put("reply", savedReply);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add reply");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{jobId}/has-reviewed")
    public ResponseEntity<Map<String, Object>> hasUserReviewed(@PathVariable Long jobId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("hasReviewed", false);
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean hasReviewed = reviewService.hasUserReviewedJob(jobId, userId);
            response.put("success", true);
            response.put("hasReviewed", hasReviewed);
        } catch (Exception e) {
            response.put("success", false);
            response.put("hasReviewed", false);
        }
        
        return ResponseEntity.ok(response);
    }
 // Add this method to ReviewController.java
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long reviewId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login to delete review");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean deleted = reviewService.deleteReview(reviewId, userId);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Review deleted successfully");
            } else {
                response.put("success", false);
                response.put("message", "Review not found or you don't have permission to delete it");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete review");
        }
        
        return ResponseEntity.ok(response);
    }
}