package com.jobportal.repository;

import com.jobportal.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByJobIdOrderByCreatedAtDesc(Long jobId);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByJobIdAndUserId(Long jobId, Long userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.jobId = ?1")
    Double findAverageRatingByJobId(Long jobId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.jobId = ?1")
    Long countByJobId(Long jobId);
    
    @Query("SELECT r.jobId, AVG(r.rating), COUNT(r) FROM Review r GROUP BY r.jobId")
    List<Object[]> findJobRatingsSummary();
}