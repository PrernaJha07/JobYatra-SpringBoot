package com.jobportal.repository;

import com.jobportal.model.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<FileDocument, String> {
    
    // Find file by ID
    Optional<FileDocument> findById(String id);
    
    // Find all files for a user
    List<FileDocument> findByUserId(Long userId);
    
    // Find files by category for a user
    List<FileDocument> findByUserIdAndFileCategory(Long userId, String fileCategory);
    
    // Find latest profile photo for user
    @Query(value = "{'userId': ?0, 'fileCategory': ?1}", sort = "{'uploadedAt': -1}")
    Optional<FileDocument> findFirstByUserIdAndFileCategoryOrderByUploadedAtDesc(Long userId, String fileCategory);
    
    // Find files by category
    List<FileDocument> findByFileCategory(String fileCategory);
    
    // Delete files by user ID
    void deleteByUserId(Long userId);
    
    // Check if file exists for user and category
    boolean existsByUserIdAndFileCategory(Long userId, String fileCategory);
}