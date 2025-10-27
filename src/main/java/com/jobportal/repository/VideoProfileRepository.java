package com.jobportal.repository;

import com.jobportal.model.VideoProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoProfileRepository extends MongoRepository<VideoProfile, String> {
    
    Optional<VideoProfile> findByUserId(Long userId);
    
    List<VideoProfile> findByUserIdAndActiveTrue(Long userId);
    
    List<VideoProfile> findByActiveTrue();
    
    @Query("{ 'expiresAt': { $lt: ?0 }, 'active': true }")
    List<VideoProfile> findExpiredVideos(LocalDateTime currentTime);
    
    long countByUserId(Long userId);
    
    @Query(value = "{ 'userId': ?0 }", delete = true)
    void deleteAllByUserId(Long userId);
    
    List<VideoProfile> findByUserEmail(String userEmail);
    
    @Query("{ 'active': true, 'expiresAt': { $gt: ?0 } }")
    List<VideoProfile> findActiveNonExpiredVideos(LocalDateTime currentTime);
}