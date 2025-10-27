package com.jobportal.controller;

import com.jobportal.model.VideoProfile;
import com.jobportal.model.User;
import com.jobportal.service.VideoProfileService;
import com.jobportal.service.FileStorageService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/video")
public class VideoProfileController {
    
    @Autowired
    private VideoProfileService videoProfileService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private UserService userService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadVideoProfile(@RequestParam("video") MultipartFile videoFile,
                                                                HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login to upload video");
            return ResponseEntity.ok(response);
        }
        
        try {
            // Validate video file
            if (videoFile.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a video file");
                return ResponseEntity.ok(response);
            }
            
            // Validate file type
            String contentType = videoFile.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                response.put("success", false);
                response.put("message", "Please upload a valid video file (MP4, WebM, etc.)");
                return ResponseEntity.ok(response);
            }
            
            // Validate file size (max 50MB)
            if (videoFile.getSize() > 50 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "Video file too large. Maximum size is 50MB");
                return ResponseEntity.ok(response);
            }
            
            // Get user details
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.ok(response);
            }
            
            User user = userOpt.get();
            
            // Store video file
            String videoPath = fileStorageService.storeVideoFile(videoFile, userId);
            
            // Create video profile
            VideoProfile videoProfile = new VideoProfile();
            videoProfile.setUserId(userId);
            videoProfile.setUserEmail(user.getEmail());
            videoProfile.setUserName(user.getFullName());
            videoProfile.setVideoUrl(videoPath);
            videoProfile.setFileSize(videoFile.getSize());
            videoProfile.setDuration("10s");
            videoProfile.setUploadedAt(LocalDateTime.now());
            videoProfile.setExpiresAt(LocalDateTime.now().plusDays(5));
            videoProfile.setActive(true);
            
            VideoProfile savedVideo = videoProfileService.saveVideoProfile(videoProfile);
            
            if (savedVideo != null && savedVideo.getId() != null) {
                response.put("success", true);
                response.put("message", "🎉 Amazing! Your 10-second video profile has been uploaded successfully! It will auto-remove in 5 days.");
                response.put("videoId", savedVideo.getId());
                response.put("videoUrl", savedVideo.getVideoUrl());
                response.put("expiresAt", savedVideo.getExpiresAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                
                System.out.println("Video profile uploaded successfully for user: " + userId);
            } else {
                response.put("success", false);
                response.put("message", "Failed to save video profile");
            }
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload video: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Video upload failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-video")
    public ResponseEntity<Map<String, Object>> getMyVideoProfile(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        try {
            Optional<VideoProfile> videoOpt = videoProfileService.getVideoProfileByUserId(userId);
            if (videoOpt.isPresent()) {
                VideoProfile video = videoOpt.get();
                
                Map<String, Object> videoData = new HashMap<>();
                videoData.put("id", video.getId());
                videoData.put("videoUrl", video.getVideoUrl());
                videoData.put("uploadedAt", video.getUploadedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                videoData.put("expiresAt", video.getExpiresAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                videoData.put("duration", video.getDuration());
                videoData.put("fileSize", video.getFileSize());
                
                response.put("success", true);
                response.put("video", videoData);
            } else {
                response.put("success", false);
                response.put("message", "No active video profile found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load video profile: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<Map<String, Object>> deleteVideoProfile(@PathVariable String videoId,
                                                                HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean deleted = videoProfileService.deleteVideoProfile(videoId, userId);
            if (deleted) {
                response.put("success", true);
                response.put("message", "✅ Video profile deleted successfully!");
            } else {
                response.put("success", false);
                response.put("message", "❌ Failed to delete video profile or access denied");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Delete failed: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stream/{userId}")
    public ResponseEntity<byte[]> streamVideo(@PathVariable Long userId, HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Check access rights
        if (currentUserId == null || (!currentUserId.equals(userId) && !Boolean.TRUE.equals(isAdmin))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        try {
            Optional<VideoProfile> videoOpt = videoProfileService.getVideoProfileByUserId(userId);
            if (videoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            VideoProfile video = videoOpt.get();
            String videoPath = video.getVideoUrl().replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir + videoPath);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] videoBytes = Files.readAllBytes(filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            headers.setContentLength(videoBytes.length);
            headers.set("Accept-Ranges", "bytes");
            
            return new ResponseEntity<>(videoBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/admin/all-videos")
    public ResponseEntity<Map<String, Object>> getAllVideosForAdmin(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (!Boolean.TRUE.equals(isAdmin)) {
            response.put("success", false);
            response.put("message", "❌ Access denied. Admin privileges required.");
            return ResponseEntity.ok(response);
        }
        
        try {
            List<VideoProfile> videos = videoProfileService.getAllActiveVideos();
            
            // Convert to response format
            List<Map<String, Object>> videoList = videos.stream().map(video -> {
                Map<String, Object> videoData = new HashMap<>();
                videoData.put("id", video.getId());
                videoData.put("userId", video.getUserId());
                videoData.put("userName", video.getUserName());
                videoData.put("userEmail", video.getUserEmail());
                videoData.put("videoUrl", video.getVideoUrl());
                videoData.put("duration", video.getDuration());
                videoData.put("uploadedAt", video.getUploadedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                videoData.put("expiresAt", video.getExpiresAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                videoData.put("fileSize", video.getFileSize());
                return videoData;
            }).toList();
            
            response.put("success", true);
            response.put("videos", videoList);
            response.put("totalVideos", videoList.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load videos: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check-access/{videoId}")
    public ResponseEntity<Map<String, Object>> checkVideoAccess(@PathVariable String videoId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        boolean canAccess = videoProfileService.canUserViewVideo(videoId, userId);
        response.put("success", canAccess);
        response.put("canView", canAccess);
        
        return ResponseEntity.ok(response);
    }
}