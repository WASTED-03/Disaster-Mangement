package com.example.disastermanagement.service.notification;

import com.example.disastermanagement.model.NotificationLog;
import com.example.disastermanagement.repository.NotificationLogRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * WebSocket implementation of NotificationService.
 * Uses STOMP messaging to send real-time notifications via WebSocket.
 */
@Service
public class WebSocketNotificationService implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationLogRepository notificationLogRepository;

    // Topic destinations
    private static final String TOPIC_ADMIN_ALERTS_BASE = "/topic/admin/alerts";
    private static final String TOPIC_USER_PREFIX = "/topic/user/";
    private static final String TOPIC_GLOBAL = "/topic/global";

    // Notification types
    private static final String TYPE_USER = "USER";
    private static final String TYPE_ADMIN = "ADMIN";
    private static final String TYPE_BROADCAST = "BROADCAST";

    // Channel type
    private static final String CHANNEL_WEBSOCKET = "WEBSOCKET";

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate,
            NotificationLogRepository notificationLogRepository) {
        this.messagingTemplate = messagingTemplate;
        this.notificationLogRepository = notificationLogRepository;
    }

    /**
     * Send a notification to a specific user by email.
     * Message is sent to /topic/user/{email}
     * 
     * @param email   The email address of the user to notify
     * @param message The notification message to send
     */
    @Override
    public void notifyUser(String email, String message) {
        if (email == null || email.isBlank()) {
            return;
        }

        try {
            String destination = TOPIC_USER_PREFIX + email;
            messagingTemplate.convertAndSend(destination, message);

            // Log notification
            logNotification(email, TYPE_USER, message, CHANNEL_WEBSOCKET, true, null);
        } catch (Exception e) {
            // Log failed notification
            logNotification(email, TYPE_USER, message, CHANNEL_WEBSOCKET, false, e.getMessage());
            System.err.println("Error sending user notification: " + e.getMessage());
        }
    }

    /**
     * Send a notification to all admin users.
     * Message is sent to /topic/admin/alerts (global)
     * 
     * @param message The notification message to send
     */
    @Override
    public void notifyAdmins(String message) {
        notifyAdmins(message, null);
    }

    /**
     * Send a notification to admin users, optionally scoped by location.
     * Message is sent to /topic/admin/alerts/{location} or
     * /topic/admin/alerts/global
     * 
     * @param message  The notification message to send
     * @param location The location scope (e.g., "Bengaluru"), or null for global
     */
    public void notifyAdmins(String message, String location) {
        try {
            String destination;
            if (location != null && !location.isBlank()) {
                // Sanitize location: Trim, Uppercase, Replace spaces with underscores
                // e.g. "New York " -> "NEW_YORK"
                String safeLocation = location.trim().toUpperCase().replace(" ", "_");
                destination = "/topic/admin/alerts/" + safeLocation;
            } else {
                destination = "/topic/admin/alerts/GLOBAL";
            }

            messagingTemplate.convertAndSend(destination, message);

            // Log notification (null email = admin broadcast)
            logNotification(null, TYPE_ADMIN, message, CHANNEL_WEBSOCKET, true, null);
        } catch (Exception e) {
            // Log failed notification
            logNotification(null, TYPE_ADMIN, message, CHANNEL_WEBSOCKET, false, e.getMessage());
            System.err.println("Error sending admin notification: " + e.getMessage());
        }
    }

    /**
     * Send a notification to all users (broadcast).
     * Message is sent to /topic/global
     * 
     * @param message The notification message to send
     */
    @Override
    public void notifyAll(String message) {
        try {
            messagingTemplate.convertAndSend(TOPIC_GLOBAL, message);

            // Log notification (null email = broadcast)
            logNotification(null, TYPE_BROADCAST, message, CHANNEL_WEBSOCKET, true, null);
        } catch (Exception e) {
            // Log failed notification
            logNotification(null, TYPE_BROADCAST, message, CHANNEL_WEBSOCKET, false, e.getMessage());
            System.err.println("Error sending broadcast notification: " + e.getMessage());
        }
    }

    /**
     * Log notification to database for audit and history.
     * 
     * @param recipientEmail The recipient email (null for broadcast/admin
     *                       notifications)
     * @param type           The notification type (USER, ADMIN, BROADCAST)
     * @param message        The notification message
     * @param channel        The channel used (WEBSOCKET, EMAIL, SMS, PUSH)
     * @param sent           Whether the notification was sent successfully
     * @param errorMessage   Error message if notification failed (null if
     *                       successful)
     */
    private void logNotification(String recipientEmail, String type, String message,
            String channel, boolean sent, String errorMessage) {
        try {
            NotificationLog log = NotificationLog.builder()
                    .recipientEmail(recipientEmail)
                    .notificationType(type)
                    .message(message)
                    .channel(channel)
                    .timestamp(LocalDateTime.now())
                    .sent(sent)
                    .errorMessage(errorMessage)
                    .build();

            notificationLogRepository.save(log);
        } catch (Exception e) {
            // Don't fail notification sending if logging fails
            System.err.println("Error logging notification: " + e.getMessage());
        }
    }
}
