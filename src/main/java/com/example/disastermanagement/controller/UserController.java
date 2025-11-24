package com.example.disastermanagement.controller;

import com.example.disastermanagement.config.JwtUtil;
import com.example.disastermanagement.model.User;
import com.example.disastermanagement.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for user-related endpoints.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Update user location (latitude and longitude).
     * 
     * Extracts user email from JWT token and updates the user's location in the database.
     * 
     * @param authHeader Authorization header containing Bearer token
     * @param body Request body containing latitude and longitude
     * @return Success response
     */
    @PostMapping("/update-location")
    public ResponseEntity<?> updateLocation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Double> body) {
        
        // Extract token from Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
        }
        
        String token = authHeader.substring(7);
        
        try {
            // Extract user email from JWT token
            String email = jwtUtil.extractUsername(token);
            
            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Get latitude and longitude from request body
            Double latitude = body.get("latitude");
            Double longitude = body.get("longitude");
            
            // Validate coordinates
            if (latitude == null || longitude == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Latitude and longitude are required"));
            }
            
            if (latitude < -90 || latitude > 90) {
                return ResponseEntity.badRequest().body(Map.of("error", "Latitude must be between -90 and 90"));
            }
            
            if (longitude < -180 || longitude > 180) {
                return ResponseEntity.badRequest().body(Map.of("error", "Longitude must be between -180 and 180"));
            }
            
            // Update user location
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Location updated successfully",
                    "latitude", latitude,
                    "longitude", longitude
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update location: " + e.getMessage()));
        }
    }
}

