package com.jobportal.service;

import com.jobportal.model.FileDocument;
import com.jobportal.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Autowired
    private FileRepository fileRepository;
    
    @Value("${file.upload-dir:./uploads/}")
    private String uploadDir;
    
    public String storeFile(MultipartFile file, Long userId, String fileCategory) throws IOException {
        System.out.println("📁 Storing file: " + file.getOriginalFilename() + " for user: " + userId + " category: " + fileCategory);
        
        // Validate file
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        
        // Validate file size (10MB limit)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("File size exceeds 10MB limit");
        }
        
        // Validate file type for CVs
        if ("cv".equals(fileCategory)) {
            String contentType = file.getContentType();
            if (contentType == null || 
                (!contentType.equals("application/pdf") && 
                 !contentType.startsWith("image/"))) {
                throw new IOException("CV must be PDF or image file");
            }
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ Created upload directory: " + uploadPath.toAbsolutePath());
        }
        
        // Generate unique filename
        String fileName = generateFileName(file.getOriginalFilename(), userId, fileCategory);
        Path filePath = uploadPath.resolve(fileName);
        
        // Store file locally
        Files.write(filePath, file.getBytes());
        System.out.println("✅ File stored locally at: " + filePath.toAbsolutePath());
        
        // Store file metadata in MongoDB
        try {
            FileDocument fileDocument = new FileDocument(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getBytes(),
                userId,
                fileCategory
            );
            
            FileDocument savedFile = fileRepository.save(fileDocument);
            System.out.println("✅ File saved to MongoDB with ID: " + savedFile.getId());
        } catch (Exception e) {
            System.err.println("❌ Failed to save to MongoDB, but file stored locally: " + e.getMessage());
            // Continue with local storage only
        }
        
        String webPath = "/uploads/" + fileName;
        return webPath;
    }
    
    public Optional<FileDocument> getFile(String fileId) {
        try {
            System.out.println("🔍 Retrieving file from MongoDB: " + fileId);
            Optional<FileDocument> file = fileRepository.findById(fileId);
            if (file.isPresent()) {
                System.out.println("✅ Retrieved file: " + file.get().getFilename());
            } else {
                System.out.println("❌ File not found: " + fileId);
            }
            return file;
        } catch (Exception e) {
            System.err.println("❌ Error retrieving file: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public byte[] getFileBytes(String filePath) throws IOException {
        System.out.println("🔍 Getting file bytes for: " + filePath);
        
        if (filePath.startsWith("/uploads/")) {
            // Local file path
            String fileName = filePath.substring("/uploads/".length());
            Path path = Paths.get(uploadDir, fileName);
            System.out.println("📁 Local file path: " + path);
            
            if (Files.exists(path)) {
                byte[] bytes = Files.readAllBytes(path);
                System.out.println("✅ Read local file: " + bytes.length + " bytes");
                return bytes;
            } else {
                throw new IOException("Local file not found: " + path);
            }
        } else if (filePath.startsWith("/api/files/")) {
            // MongoDB file ID
            String fileId = filePath.substring("/api/files/".length());
            Optional<FileDocument> fileDoc = getFile(fileId);
            if (fileDoc.isPresent()) {
                byte[] data = fileDoc.get().getData();
                System.out.println("✅ Read MongoDB file: " + data.length + " bytes");
                return data;
            } else {
                throw new IOException("MongoDB file not found: " + fileId);
            }
        }
        throw new IOException("Invalid file path format: " + filePath);
    }
    
    private String generateFileName(String originalFileName, Long userId, String fileCategory) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            System.out.println("🔍 Detected file extension: " + extension);
        }
        
        String baseName = fileCategory + "_" + userId + "_" + System.currentTimeMillis();
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        String fileName = baseName + "_" + randomId + extension;
        
        System.out.println("📄 Generated filename: " + fileName);
        return fileName;
    }
    
    public boolean validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        boolean isValid = contentType != null && (
            contentType.startsWith("image/") || 
            contentType.equals("application/pdf")
        );
        
        System.out.println("🔍 File validation - Type: " + contentType + ", Valid: " + isValid);
        return isValid;
    }
    
    public String storeVideoFile(MultipartFile file, Long userId) throws IOException {
        System.out.println("🎥 Storing video file for user: " + userId);
        
        // Validate file
        if (file.isEmpty()) {
            throw new IOException("Video file is empty");
        }
        
        // Validate video file size (50MB limit for videos)
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IOException("Video file size exceeds 50MB limit");
        }
        
        // Validate video file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IOException("File must be a video");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ Created upload directory: " + uploadPath.toAbsolutePath());
        }
        
        // Generate unique filename
        String fileName = "video_" + userId + "_" + UUID.randomUUID() + 
                         getFileExtension(file.getOriginalFilename());
        
        // Store file locally
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());
        System.out.println("✅ Video stored locally at: " + filePath.toAbsolutePath());
        
        // Also store in MongoDB for backup
        FileDocument fileDocument = new FileDocument(
            fileName,
            file.getContentType(),
            file.getSize(),
            file.getBytes(),
            userId,
            "video_profile"
        );
        
        fileRepository.save(fileDocument);
        System.out.println("✅ Video saved to MongoDB");
        
        return "/uploads/" + fileName;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastIndex = fileName.lastIndexOf(".");
        return lastIndex == -1 ? "" : fileName.substring(lastIndex);
    }
}