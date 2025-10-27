package com.jobportal.controller;

import com.jobportal.model.FileDocument;
import com.jobportal.model.User;
import com.jobportal.service.FileStorageService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class FileController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/upload-photo")
    public ResponseEntity<Map<String, Object>> uploadPhoto(@RequestParam("photo") MultipartFile file,
                                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null || userId == 0) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        try {
            // Validate file type
            if (!isImageFile(file)) {
                response.put("success", false);
                response.put("message", "Please upload a valid image file (JPEG, PNG, JPG, GIF)");
                return ResponseEntity.ok(response);
            }
            
            String filePath = fileStorageService.storeFile(file, userId, "profile_photo");
            
            // Update user profile photo path
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setProfilePhotoPath(filePath);
                User updatedUser = userService.updateUser(user);
                
                if (updatedUser != null) {
                    response.put("success", true);
                    response.put("message", "Photo uploaded successfully");
                    response.put("photoPath", filePath);
                    System.out.println("✅ Profile photo updated for user ID: " + userId + " at path: " + filePath);
                } else {
                    response.put("success", false);
                    response.put("message", "Failed to update profile photo in database");
                }
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload photo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/files/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileId) {
        try {
            System.out.println("📥 Requested file ID: " + fileId);
            
            Optional<FileDocument> fileOpt = fileStorageService.getFile(fileId);
            if (fileOpt.isPresent()) {
                FileDocument file = fileOpt.get();
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(file.getFileType()));
                headers.setContentDispositionFormData("inline", file.getFilename());
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                headers.setContentLength(file.getData().length);
                
                System.out.println("✅ Serving file from MongoDB: " + fileId + " - " + file.getFilename() + " (" + file.getFileType() + ")");
                return new ResponseEntity<>(file.getData(), headers, HttpStatus.OK);
            } else {
                System.err.println("❌ File not found in MongoDB: " + fileId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("❌ Error serving file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // New endpoint to get file info
    @GetMapping("/files/{fileId}/info")
    public ResponseEntity<Map<String, Object>> getFileInfo(@PathVariable String fileId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<FileDocument> fileOpt = fileStorageService.getFile(fileId);
            if (fileOpt.isPresent()) {
                FileDocument file = fileOpt.get();
                response.put("success", true);
                response.put("filename", file.getFilename());
                response.put("fileType", file.getFileType());
                response.put("fileSize", file.getFileSize());
                response.put("uploadedAt", file.getUploadedAt());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "File not found");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting file info: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<byte[]> getUploadedFile(@PathVariable String filename) {
        try {
            byte[] fileBytes = fileStorageService.getFileBytes("/uploads/" + filename);
            
            HttpHeaders headers = new HttpHeaders();
            
            // Determine content type based on file extension
            String contentType = determineContentType(filename);
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("inline", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            System.out.println("✅ Serving local file: " + filename);
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            System.err.println("❌ Local file not found: " + filename);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error serving local file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/jpg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif")
        );
    }
    
 // Helper method to determine content type
    private String determineContentType(String filename) {
        String lowerFilename = filename.toLowerCase();
        
        if (lowerFilename.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    
    }
    @PostMapping("/debug-upload")
    public ResponseEntity<Map<String, Object>> debugUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("filename", file.getOriginalFilename());
            response.put("contentType", file.getContentType());
            response.put("size", file.getSize());
            response.put("isEmpty", file.isEmpty());
            response.put("success", true);
            response.put("message", "File analyzed successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error analyzing file: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}