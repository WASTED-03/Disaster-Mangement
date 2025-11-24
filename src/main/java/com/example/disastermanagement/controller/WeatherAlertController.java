package com.example.disastermanagement.controller;

import com.example.disastermanagement.config.JwtUtil;
import com.example.disastermanagement.model.UserAlert;
import com.example.disastermanagement.model.WeatherAlert;
import com.example.disastermanagement.repository.UserAlertRepository;
import com.example.disastermanagement.repository.WeatherAlertRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for managing weather alerts stored in the database.
 */
@RestController
@RequestMapping("/alerts")
public class WeatherAlertController {

    private final WeatherAlertRepository weatherAlertRepository;
    private final UserAlertRepository userAlertRepository;
    private final JwtUtil jwtUtil;

    public WeatherAlertController(WeatherAlertRepository weatherAlertRepository,
                                 UserAlertRepository userAlertRepository,
                                 JwtUtil jwtUtil) {
        this.weatherAlertRepository = weatherAlertRepository;
        this.userAlertRepository = userAlertRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * A. Get all weather alerts (Admin only)
     * 
     * Returns all alerts from the database, ordered by timestamp descending.
     * 
     * @return List of all weather alerts
     */
    @GetMapping("/all")
    public ResponseEntity<List<WeatherAlert>> getAllAlerts() {
        List<WeatherAlert> alerts = weatherAlertRepository.findAllByOrderByTimestampDesc();
        return ResponseEntity.ok(alerts);
    }

    /**
     * B. Get recent weather alerts (User accessible)
     * 
     * Returns alerts from the last N hours, ordered by timestamp descending.
     * 
     * @param hours Number of hours to look back (default: 6)
     * @return List of recent weather alerts
     */
    @GetMapping("/recent")
    public ResponseEntity<List<WeatherAlert>> getRecentAlerts(
            @RequestParam(defaultValue = "6") int hours) {
        
        // Calculate timestamp for N hours ago
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        
        // Fetch alerts after the cutoff time
        List<WeatherAlert> alerts = weatherAlertRepository.findByTimestampAfterOrderByTimestampDesc(cutoffTime);
        
        return ResponseEntity.ok(alerts);
    }

    /**
     * STEP 10F: Get user's personal alerts
     * 
     * Returns alerts specific to the logged-in user based on their location.
     * Extracts user email from JWT token.
     * 
     * @param authHeader Authorization header containing Bearer token
     * @return List of user's alerts with message, timestamp, and severity
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyAlerts(
            @RequestHeader("Authorization") String authHeader) {
        
        // Extract token from Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
        }
        
        String token = authHeader.substring(7);
        
        try {
            // Extract user email from JWT token
            String userEmail = jwtUtil.extractUsername(token);
            
            // Get all alerts for this user, ordered by timestamp descending
            List<UserAlert> userAlerts = userAlertRepository.findByUserEmailOrderByTimestampDesc(userEmail);
            
            // Convert to simplified format
            List<Map<String, Object>> alertList = userAlerts.stream()
                    .map(alert -> {
                        Map<String, Object> alertMap = new HashMap<>();
                        alertMap.put("message", alert.getAlertMessage());
                        alertMap.put("timestamp", alert.getTimestamp());
                        alertMap.put("severity", alert.getSeverity());
                        alertMap.put("type", alert.getType());
                        return alertMap;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(alertList);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch alerts: " + e.getMessage()));
        }
    }
}
