package com.jobportal.service;

import com.jobportal.model.Review;
import com.jobportal.model.ReviewReply;
import com.jobportal.repository.ReviewRepository;
import com.jobportal.repository.ReviewReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ReviewReplyRepository reviewReplyRepository;
    
    public Review addReview(Review review) {
        // Check if user already reviewed this job
        Optional<Review> existingReview = reviewRepository.findByJobIdAndUserId(
            review.getJobId(), review.getUserId());
        
        if (existingReview.isPresent()) {
            throw new RuntimeException("You have already reviewed this job");
        }
        
        Review savedReview = reviewRepository.save(review);
        
        // Add auto-reply from system
        ReviewReply autoReply = new ReviewReply(
            savedReview.getId(),
            "CareerConnect Team",
            "/uploads/system-avatar.png",
            "Thank you for your valuable feedback! We appreciate you taking the time to share your experience.",
            true
        );
        
        reviewReplyRepository.save(autoReply);
        
        return savedReview;
    }
    
    public List<Review> getReviewsByJobId(Long jobId) {
        List<Review> reviews = reviewRepository.findByJobIdOrderByCreatedAtDesc(jobId);
        
        // Load replies for each review
        for (Review review : reviews) {
            List<ReviewReply> replies = reviewReplyRepository.findByReviewIdOrderByCreatedAtAsc(review.getId());
            review.setReplies(replies);
        }
        
        return reviews;
    }
    
    public Map<String, Object> getJobRatingSummary(Long jobId) {
        Map<String, Object> summary = new HashMap<>();
        
        Double avgRating = reviewRepository.findAverageRatingByJobId(jobId);
        Long reviewCount = reviewRepository.countByJobId(jobId);
        
        summary.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        summary.put("reviewCount", reviewCount != null ? reviewCount : 0);
        
        return summary;
    }
    
    public Map<Long, Map<String, Object>> getAllJobsRatingSummary() {
        List<Object[]> results = reviewRepository.findJobRatingsSummary();
        Map<Long, Map<String, Object>> summaries = new HashMap<>();
        
        for (Object[] result : results) {
            Long jobId = (Long) result[0];
            Double avgRating = (Double) result[1];
            Long count = (Long) result[2];
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
            summary.put("reviewCount", count != null ? count : 0);
            
            summaries.put(jobId, summary);
        }
        
        return summaries;
    }
    
    public ReviewReply addReplyToReview(Long reviewId, ReviewReply reply) {
        reply.setReviewId(reviewId);
        return reviewReplyRepository.save(reply);
    }
    
    public boolean hasUserReviewedJob(Long jobId, Long userId) {
        return reviewRepository.findByJobIdAndUserId(jobId, userId).isPresent();
    }
 // Add this method to ReviewService.java
    public boolean deleteReview(Long reviewId, Long userId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            
            // Check if the user owns this review
            if (review.getUserId().equals(userId)) {
                // Delete associated replies first
                List<ReviewReply> replies = reviewReplyRepository.findByReviewIdOrderByCreatedAtAsc(reviewId);
                reviewReplyRepository.deleteAll(replies);
                
                // Delete the review
                reviewRepository.delete(review);
                return true;
            }
        }
        return false;
    }
}