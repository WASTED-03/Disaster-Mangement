package com.example.disastermanagement.controller;

import com.example.disastermanagement.config.JwtUtil;
import com.example.disastermanagement.model.User;
import com.example.disastermanagement.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for managing FCM (Firebase Cloud Messaging) tokens.
 * 
 * Users can register/update their FCM token to receive push notifications on mobile devices.
 */
@RestController
@RequestMapping("/user/fcm")
public class FcmTokenController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public FcmTokenController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register or update FCM token for the logged-in user.
     * 
     * POST /user/fcm/register
     * Headers: Authorization: Bearer <token>
     * Body: { "fcmToken": "your-fcm-token-here" }
     * 
     * @param authHeader Authorization header containing JWT token
     * @param body Request body containing FCM token
     * @return Success response
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerFcmToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        
        try {
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractUsername(token);

            if (userEmail == null || !jwtUtil.validateToken(token, userEmail)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            String fcmToken = body.get("fcmToken");
            if (fcmToken == null || fcmToken.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "FCM token is required"));
            }

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setFcmToken(fcmToken);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "FCM token registered successfully",
                    "email", userEmail
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to register FCM token: " + e.getMessage()));
        }
    }

    /**
     * Remove FCM token for the logged-in user (logout/unregister device).
     * 
     * DELETE /user/fcm/unregister
     * Headers: Authorization: Bearer <token>
     * 
     * @param authHeader Authorization header containing JWT token
     * @return Success response
     */
    @DeleteMapping("/unregister")
    public ResponseEntity<?> unregisterFcmToken(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractUsername(token);

            if (userEmail == null || !jwtUtil.validateToken(token, userEmail)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setFcmToken(null);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "FCM token unregistered successfully",
                    "email", userEmail
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to unregister FCM token: " + e.getMessage()));
        }
    }
}

