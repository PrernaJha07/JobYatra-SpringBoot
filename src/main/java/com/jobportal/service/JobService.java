package com.jobportal.service;

import com.jobportal.model.Job;
import com.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    public Job createJob(Job job) {
        try {
            System.out.println("💾 Saving job to database: " + job.getTitle());
            Job savedJob = jobRepository.save(job);
            System.out.println("✅ Job saved successfully with ID: " + savedJob.getId());
            return savedJob;
        } catch (Exception e) {
            System.err.println("❌ Failed to save job: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Job> getAllActiveJobs() {
        try {
            List<Job> jobs = jobRepository.findByActiveTrue();
            System.out.println("📋 Retrieved " + jobs.size() + " active jobs");
            return jobs;
        } catch (Exception e) {
            System.err.println("❌ Failed to get active jobs: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public Optional<Job> getJobById(Long id) {
        try {
            Optional<Job> job = jobRepository.findById(id);
            if (job.isPresent()) {
                System.out.println("✅ Found job with ID: " + id);
            } else {
                System.out.println("❌ Job not found with ID: " + id);
            }
            return job;
        } catch (Exception e) {
            System.err.println("❌ Failed to get job by ID: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Job> searchJobs(String query) {
        try {
            List<Job> results = new ArrayList<>();
            results.addAll(jobRepository.findByTitleContainingIgnoreCase(query));
            results.addAll(jobRepository.findByCompanyContainingIgnoreCase(query));
            results.addAll(jobRepository.findByLocationContainingIgnoreCase(query));
            
            // Remove duplicates
            List<Job> distinctResults = results.stream().distinct().toList();
            System.out.println("🔍 Search found " + distinctResults.size() + " jobs for query: " + query);
            return distinctResults;
        } catch (Exception e) {
            System.err.println("❌ Search failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Job> getAllJobs() {
        try {
            List<Job> jobs = jobRepository.findAll();
            System.out.println("📋 Retrieved " + jobs.size() + " total jobs");
            return jobs;
        } catch (Exception e) {
            System.err.println("❌ Failed to get all jobs: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public long getTotalJobs() {
        try {
            long count = jobRepository.count();
            System.out.println("📊 Total jobs count: " + count);
            return count;
        } catch (Exception e) {
            System.err.println("❌ Failed to count jobs: " + e.getMessage());
            return 0;
        }
    }
    
    public boolean deleteJob(Long jobId) {
        try {
            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isPresent()) {
                Job job = jobOpt.get();
                job.setActive(false);
                jobRepository.save(job);
                System.out.println("✅ Job soft deleted: " + jobId);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Failed to delete job: " + e.getMessage());
            return false;
        }
    }
    
}