package com.jobportal.repository;

import com.jobportal.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Feedback> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT f FROM Feedback f WHERE f.userId = ?1 AND f.readStatus = false AND f.adminReply IS NOT NULL")
    List<Feedback> findUnreadRepliesByUserId(Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Feedback f SET f.readStatus = true WHERE f.userId = ?1 AND f.readStatus = false")
    void markAllAsReadByUserId(Long userId);
    
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.userId = ?1 AND f.readStatus = false AND f.adminReply IS NOT NULL")
    long countByUserIdAndReadStatusFalseAndAdminReplyIsNotNull(Long userId);
}