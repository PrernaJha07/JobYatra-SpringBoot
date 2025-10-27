package com.jobportal.repository;

import com.jobportal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByActiveTrue();
    List<Job> findByCompanyContainingIgnoreCase(String company);
    List<Job> findByTitleContainingIgnoreCase(String title);
    List<Job> findByLocationContainingIgnoreCase(String location);
    
    @Query("SELECT j FROM Job j ORDER BY j.createdAt DESC")
    List<Job> findAllOrderByCreatedAtDesc();
    
    @Modifying
    @Transactional
    @Query("UPDATE Job j SET j.active = :active WHERE j.id = :id")
    void updateJobStatus(@Param("id") Long id, @Param("active") boolean active);
    
    long countByActiveTrue();
    
    // ✅ NEW METHOD: Check for duplicate jobs
    @Query("SELECT COUNT(j) > 0 FROM Job j WHERE LOWER(j.title) = LOWER(:title) AND LOWER(j.company) = LOWER(:company) AND j.active = true")
    boolean existsByTitleAndCompanyAndActiveTrue(@Param("title") String title, @Param("company") String company);
}