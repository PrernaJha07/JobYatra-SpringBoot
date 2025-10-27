package com.jobportal.service;

import com.jobportal.model.Feedback;
import com.jobportal.model.User;
import com.jobportal.repository.FeedbackRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    public Feedback submitFeedback(Long userId, String feedbackText, String photoPath) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            
            User user = userOpt.get();
            Feedback feedback = new Feedback(userId, user.getEmail(), feedbackText);
            feedback.setPhotoPath(photoPath);
            
            Feedback savedFeedback = feedbackRepository.save(feedback);
            System.out.println("Feedback saved with ID: " + savedFeedback.getId());
            return savedFeedback;
            
        } catch (Exception e) {
            System.err.println("Error saving feedback: " + e.getMessage());
            throw new RuntimeException("Failed to save feedback: " + e.getMessage());
        }
    }
    
    public List<Feedback> getUserFeedback(Long userId) {
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }
    
    public Feedback replyToFeedback(Long feedbackId, String adminReply) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isPresent()) {
            Feedback feedback = feedbackOpt.get();
            feedback.setAdminReply(adminReply);
            feedback.setReadStatus(false);
            return feedbackRepository.save(feedback);
        }
        throw new RuntimeException("Feedback not found with ID: " + feedbackId);
    }
    
    public void deleteFeedback(Long feedbackId) {
        if (feedbackRepository.existsById(feedbackId)) {
            feedbackRepository.deleteById(feedbackId);
            System.out.println("Feedback deleted with ID: " + feedbackId);
        } else {
            throw new RuntimeException("Feedback not found with ID: " + feedbackId);
        }
    }
    
    public boolean hasNewReplies(Long userId) {
        return feedbackRepository.countByUserIdAndReadStatusFalseAndAdminReplyIsNotNull(userId) > 0;
    }
    
    public void markFeedbackAsRead(Long userId) {
        feedbackRepository.markAllAsReadByUserId(userId);
    }
}