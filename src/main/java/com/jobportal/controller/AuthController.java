package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.AdminService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String email, 
                                                   @RequestParam String password, 
                                                   HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        // Check admin login
        if (adminService.isAdminUser(email, password)) {
            session.setAttribute("userId", 0L);
            session.setAttribute("isAdmin", true);
            
            // Create admin user data
            Map<String, Object> adminData = new HashMap<>();
            adminData.put("id", 0L);
            adminData.put("fullName", "Admin User");
            adminData.put("email", email);
            adminData.put("isAdmin", true);
            session.setAttribute("user", adminData);
            
            response.put("success", true);
            response.put("isAdmin", true);
            response.put("message", "Admin login successful!");
            return ResponseEntity.ok(response);
        }
        
        // Check regular user login
        Optional<User> user = userService.loginUser(email, password);
        if (user.isPresent()) {
            User userData = user.get();
            session.setAttribute("userId", userData.getId());
            session.setAttribute("isAdmin", false);
            
            // Create user session data
            Map<String, Object> userSessionData = new HashMap<>();
            userSessionData.put("id", userData.getId());
            userSessionData.put("fullName", userData.getFullName());
            userSessionData.put("email", userData.getEmail());
            userSessionData.put("address", userData.getAddress() != null ? userData.getAddress() : "");
            userSessionData.put("phone", userData.getPhone() != null ? userData.getPhone() : "");
            userSessionData.put("profilePhotoPath", userData.getProfilePhotoPath() != null ? userData.getProfilePhotoPath() : "");
            userSessionData.put("isAdmin", false);
            session.setAttribute("user", userSessionData);
            
            response.put("success", true);
            response.put("isAdmin", false);
            response.put("message", "Login successful!");
        } else {
            response.put("success", false);
            response.put("message", "Invalid email or password");
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestParam String fullName,
                                                    @RequestParam String email,
                                                    @RequestParam String password,
                                                    @RequestParam String address,
                                                    @RequestParam String phone,
                                                    HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        if (userService.emailExists(email)) {
            response.put("success", false);
            response.put("message", "Email already exists");
            return ResponseEntity.ok(response);
        }
        
        try {
            User user = new User(fullName, email, password, address, phone);
            User savedUser = userService.registerUser(user);
            
            if (savedUser != null && savedUser.getId() != null) {
                session.setAttribute("userId", savedUser.getId());
                session.setAttribute("isAdmin", false);
                
                // Create user session data
                Map<String, Object> userSessionData = new HashMap<>();
                userSessionData.put("id", savedUser.getId());
                userSessionData.put("fullName", savedUser.getFullName());
                userSessionData.put("email", savedUser.getEmail());
                userSessionData.put("address", savedUser.getAddress() != null ? savedUser.getAddress() : "");
                userSessionData.put("phone", savedUser.getPhone() != null ? savedUser.getPhone() : "");
                userSessionData.put("profilePhotoPath", savedUser.getProfilePhotoPath() != null ? savedUser.getProfilePhotoPath() : "");
                userSessionData.put("isAdmin", false);
                session.setAttribute("user", userSessionData);
                
                response.put("success", true);
                response.put("message", "Account created successfully!");
                System.out.println("✅ User registered with ID: " + savedUser.getId());
            } else {
                response.put("success", false);
                response.put("message", "Failed to create account");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Object>> checkAuth(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        response.put("loggedIn", userId != null);
        response.put("isAdmin", Boolean.TRUE.equals(isAdmin));
        
        if (userId != null && userId != 0) {
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                User userData = user.get();
                
                // Use HashMap instead of Map.of to handle null values safely
                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("id", userData.getId());
                userResponse.put("fullName", userData.getFullName() != null ? userData.getFullName() : "");
                userResponse.put("email", userData.getEmail() != null ? userData.getEmail() : "");
                userResponse.put("address", userData.getAddress() != null ? userData.getAddress() : "");
                userResponse.put("phone", userData.getPhone() != null ? userData.getPhone() : "");
                userResponse.put("profilePhotoPath", userData.getProfilePhotoPath() != null ? userData.getProfilePhotoPath() : "");
                
                response.put("user", userResponse);
                
                // Update session with fresh data
                Map<String, Object> userSessionData = new HashMap<>();
                userSessionData.put("id", userData.getId());
                userSessionData.put("fullName", userData.getFullName() != null ? userData.getFullName() : "");
                userSessionData.put("email", userData.getEmail() != null ? userData.getEmail() : "");
                userSessionData.put("address", userData.getAddress() != null ? userData.getAddress() : "");
                userSessionData.put("phone", userData.getPhone() != null ? userData.getPhone() : "");
                userSessionData.put("profilePhotoPath", userData.getProfilePhotoPath() != null ? userData.getProfilePhotoPath() : "");
                userSessionData.put("isAdmin", false);
                session.setAttribute("user", userSessionData);
            }
        } else if (userId != null && userId == 0L && Boolean.TRUE.equals(isAdmin)) {
            // Admin user - get from session
            Map<String, Object> adminData = (Map<String, Object>) session.getAttribute("user");
            if (adminData != null) {
                response.put("user", adminData);
            } else {
                // Create default admin data if not in session
                Map<String, Object> defaultAdminData = new HashMap<>();
                defaultAdminData.put("id", 0L);
                defaultAdminData.put("fullName", "Admin User");
                defaultAdminData.put("email", "abhi@gmail.com");
                defaultAdminData.put("isAdmin", true);
                response.put("user", defaultAdminData);
                session.setAttribute("user", defaultAdminData);
            }
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userService.getUserByEmail(email);
        
        if (user.isPresent()) {
            response.put("success", true);
            response.put("password", user.get().getPassword());
            response.put("message", "Password retrieved successfully");
        } else {
            response.put("success", false);
            response.put("message", "Email not found in our database");
        }
        return ResponseEntity.ok(response);
    }
}