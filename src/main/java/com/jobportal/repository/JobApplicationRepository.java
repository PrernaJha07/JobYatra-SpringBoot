package com.jobportal.repository;

import com.jobportal.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(Long userId);
    List<JobApplication> findByJobId(Long jobId);
    Optional<JobApplication> findByUserIdAndJobId(Long userId, Long jobId);
    long countByStatus(String status);
    List<JobApplication> findAllByOrderByAppliedAtDesc();
}