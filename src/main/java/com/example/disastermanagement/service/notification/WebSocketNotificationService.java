package com.example.disastermanagement.service.notification;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket implementation of NotificationService.
 * Uses STOMP messaging to send real-time notifications via WebSocket.
 */
@Service
public class WebSocketNotificationService implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Topic destinations
    private static final String TOPIC_ADMINS = "/topic/admins";
    private static final String TOPIC_USER_PREFIX = "/topic/user/";
    private static final String TOPIC_GLOBAL = "/topic/global";

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a notification to a specific user by email.
     * Message is sent to /topic/user/{email}
     * 
     * @param email The email address of the user to notify
     * @param message The notification message to send
     */
    @Override
    public void notifyUser(String email, String message) {
        if (email == null || email.isBlank()) {
            return;
        }
        
        String destination = TOPIC_USER_PREFIX + email;
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Send a notification to all admin users.
     * Message is sent to /topic/admins
     * 
     * @param message The notification message to send
     */
    @Override
    public void notifyAdmins(String message) {
        messagingTemplate.convertAndSend(TOPIC_ADMINS, message);
    }

    /**
     * Send a notification to all users (broadcast).
     * Message is sent to /topic/global
     * 
     * @param message The notification message to send
     */
    @Override
    public void notifyAll(String message) {
        messagingTemplate.convertAndSend(TOPIC_GLOBAL, message);
    }
}

