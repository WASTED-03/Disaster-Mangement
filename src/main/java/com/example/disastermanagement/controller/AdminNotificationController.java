package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.NotificationLog;
import com.example.disastermanagement.repository.NotificationLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for viewing notification logs and history.
 */
@RestController
@RequestMapping("/admin/notifications")
public class AdminNotificationController {

    private final NotificationLogRepository notificationLogRepository;

    public AdminNotificationController(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    /**
     * STEP 12.11: Get all notification logs (Admin only)
     * 
     * Returns all notification logs from the database, ordered by timestamp descending.
     * 
     * @return List of all notification logs
     */
    @GetMapping
    public ResponseEntity<List<NotificationLog>> getAllNotificationLogs() {
        List<NotificationLog> logs = notificationLogRepository.findAllByOrderByTimestampDesc();
        return ResponseEntity.ok(logs);
    }

    /**
     * Get notification logs by type (USER, ADMIN, BROADCAST).
     * 
     * @param type The notification type
     * @return List of notification logs of the specified type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationLog>> getNotificationLogsByType(@PathVariable String type) {
        List<NotificationLog> logs = notificationLogRepository.findByNotificationTypeOrderByTimestampDesc(type);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get notification logs for a specific user.
     * 
     * @param email The user email
     * @return List of notification logs for the user
     */
    @GetMapping("/user/{email}")
    public ResponseEntity<List<NotificationLog>> getNotificationLogsByUser(@PathVariable String email) {
        List<NotificationLog> logs = notificationLogRepository.findByRecipientEmailOrderByTimestampDesc(email);
        return ResponseEntity.ok(logs);
    }
}

