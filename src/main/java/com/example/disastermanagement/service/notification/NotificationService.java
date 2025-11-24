package com.example.disastermanagement.service.notification;

/**
 * Interface for notification services.
 * Defines methods for sending notifications to users, admins, and all users.
 */
public interface NotificationService {

    /**
     * Send a notification to a specific user by email.
     * 
     * @param email The email address of the user to notify
     * @param message The notification message to send
     */
    void notifyUser(String email, String message);

    /**
     * Send a notification to all admin users.
     * 
     * @param message The notification message to send
     */
    void notifyAdmins(String message);

    /**
     * Send a notification to all users (broadcast).
     * 
     * @param message The notification message to send
     */
    void notifyAll(String message);
}

