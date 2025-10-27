package com.jobportal.service;

import com.jobportal.model.VideoProfile;
import com.jobportal.repository.VideoProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VideoProfileService {
    
    @Autowired
    private VideoProfileRepository videoProfileRepository;
    
    public VideoProfile saveVideoProfile(VideoProfile videoProfile) {
        try {
            // Deactivate any existing active video for this user
            Optional<VideoProfile> existingVideo = videoProfileRepository.findByUserId(videoProfile.getUserId());
            if (existingVideo.isPresent()) {
                VideoProfile existing = existingVideo.get();
                if (existing.isActive()) {
                    existing.setActive(false);
                    videoProfileRepository.save(existing);
                    System.out.println("Deactivated existing video for user: " + videoProfile.getUserId());
                }
            }
            
            VideoProfile saved = videoProfileRepository.save(videoProfile);
            System.out.println("Saved new video profile for user: " + videoProfile.getUserId() + " with ID: " + saved.getId());
            return saved;
            
        } catch (Exception e) {
            System.err.println("Error saving video profile: " + e.getMessage());
            throw e;
        }
    }
    
    public Optional<VideoProfile> getVideoProfileByUserId(Long userId) {
        try {
            return videoProfileRepository.findByUserIdAndActiveTrue(userId)
                    .stream()
                    .findFirst()
                    .filter(video -> !video.isExpired() && video.isActive());
        } catch (Exception e) {
            System.err.println("Error getting video profile for user " + userId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<VideoProfile> getAllActiveVideos() {
        try {
            return videoProfileRepository.findByActiveTrue()
                    .stream()
                    .filter(video -> !video.isExpired())
                    .toList();
        } catch (Exception e) {
            System.err.println("Error getting all active videos: " + e.getMessage());
            return List.of();
        }
    }
    
    public boolean deleteVideoProfile(String videoId, Long userId) {
        try {
            Optional<VideoProfile> videoOpt = videoProfileRepository.findById(videoId);
            if (videoOpt.isPresent()) {
                VideoProfile video = videoOpt.get();
                // Check if user owns the video
                if (video.getUserId().equals(userId)) {
                    video.setActive(false);
                    videoProfileRepository.save(video);
                    System.out.println("Deleted video profile: " + videoId + " for user: " + userId);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting video profile: " + e.getMessage());
            return false;
        }
    }
    
    public boolean canUserViewVideo(String videoId, Long userId) {
        try {
            Optional<VideoProfile> videoOpt = videoProfileRepository.findById(videoId);
            if (videoOpt.isPresent()) {
                VideoProfile video = videoOpt.get();
                // Only the owner or admin can view
                return video.getUserId().equals(userId) || userId == 0L;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking video access: " + e.getMessage());
            return false;
        }
    }
    
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Run every 24 hours
    public void cleanupExpiredVideos() {
        try {
            List<VideoProfile> expiredVideos = videoProfileRepository.findExpiredVideos(LocalDateTime.now());
            int count = 0;
            for (VideoProfile video : expiredVideos) {
                if (video.isActive()) {
                    video.setActive(false);
                    videoProfileRepository.save(video);
                    count++;
                    System.out.println("Auto-deleted expired video: " + video.getId() + " for user: " + video.getUserId());
                }
            }
            if (count > 0) {
                System.out.println("Cleanup completed: " + count + " expired videos removed");
            }
        } catch (Exception e) {
            System.err.println("Error in video cleanup: " + e.getMessage());
        }
    }
    
    public long getActiveVideoCount() {
        try {
            return videoProfileRepository.findByActiveTrue()
                    .stream()
                    .filter(video -> !video.isExpired())
                    .count();
        } catch (Exception e) {
            System.err.println("Error counting active videos: " + e.getMessage());
            return 0;
        }
    }
}