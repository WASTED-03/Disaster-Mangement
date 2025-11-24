package com.example.disastermanagement.service.notification;

import com.example.disastermanagement.model.NotificationLog;
import com.example.disastermanagement.model.User;
import com.example.disastermanagement.repository.NotificationLogRepository;
import com.example.disastermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * STEP 12.12: FCM (Firebase Cloud Messaging) Push Notification Service
 * 
 * This service handles sending push notifications to Android/iOS devices via Firebase.
 * 
 * TODO: To implement fully, you need to:
 * 1. Add Firebase Admin SDK dependency to pom.xml:
 *    <dependency>
 *        <groupId>com.google.firebase</groupId>
 *        <artifactId>firebase-admin</artifactId>
 *        <version>9.2.0</version>
 *    </dependency>
 * 
 * 2. Download Firebase service account JSON key from Firebase Console
 * 3. Place it in src/main/resources/firebase-service-account.json
 * 4. Initialize Firebase Admin SDK in a @Configuration class
 * 5. Implement the sendPushNotification() method using Firebase Admin SDK
 * 
 * For now, this is a stub implementation that logs notifications.
 */
@Service
public class FcmPushNotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;

    private static final String CHANNEL_PUSH = "PUSH";
    private static final String TYPE_USER = "USER";
    private static final String TYPE_ADMIN = "ADMIN";
    private static final String TYPE_BROADCAST = "BROADCAST";

    public FcmPushNotificationService(NotificationLogRepository notificationLogRepository,
                                     UserRepository userRepository) {
        this.notificationLogRepository = notificationLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * Send push notification to a specific user by email.
     * 
     * @param email The user's email address
     * @param title The notification title
     * @param message The notification message body
     */
    public void sendPushToUser(String email, String title, String message) {
        if (email == null || email.isBlank()) {
            return;
        }

        try {
            // Get user's FCM token
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null || user.getFcmToken() == null || user.getFcmToken().isBlank()) {
                // User not found or no FCM token registered
                logNotification(email, TYPE_USER, title + ": " + message, CHANNEL_PUSH, false, 
                        "User not found or FCM token not registered");
                return;
            }

            // TODO: Implement actual FCM push notification using Firebase Admin SDK
            // Example code (commented out):
            // FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
            // Message fcmMessage = Message.builder()
            //     .setToken(user.getFcmToken())
            //     .setNotification(Notification.builder()
            //         .setTitle(title)
            //         .setBody(message)
            //         .build())
            //     .build();
            // firebaseMessaging.send(fcmMessage);

            // For now, just log the notification
            System.out.println("FCM Push Notification (STUB):");
            System.out.println("  To: " + email);
            System.out.println("  Token: " + user.getFcmToken());
            System.out.println("  Title: " + title);
            System.out.println("  Message: " + message);

            logNotification(email, TYPE_USER, title + ": " + message, CHANNEL_PUSH, true, null);
        } catch (Exception e) {
            logNotification(email, TYPE_USER, title + ": " + message, CHANNEL_PUSH, false, e.getMessage());
            System.err.println("Error sending FCM push notification: " + e.getMessage());
        }
    }

    /**
     * Send push notification to all admin users.
     * 
     * @param title The notification title
     * @param message The notification message body
     */
    public void sendPushToAdmins(String title, String message) {
        try {
            // Get all admin users with FCM tokens
            List<User> admins = userRepository.findAll().stream()
                    .filter(user -> user.getRoleSet().contains("ADMIN"))
                    .filter(user -> user.getFcmToken() != null && !user.getFcmToken().isBlank())
                    .toList();

            for (User admin : admins) {
                sendPushToUser(admin.getEmail(), title, message);
            }

            logNotification(null, TYPE_ADMIN, title + ": " + message, CHANNEL_PUSH, true, null);
        } catch (Exception e) {
            logNotification(null, TYPE_ADMIN, title + ": " + message, CHANNEL_PUSH, false, e.getMessage());
            System.err.println("Error sending FCM push to admins: " + e.getMessage());
        }
    }

    /**
     * Send push notification to all users (broadcast).
     * 
     * @param title The notification title
     * @param message The notification message body
     */
    public void sendPushToAll(String title, String message) {
        try {
            // Get all users with FCM tokens
            List<User> users = userRepository.findAll().stream()
                    .filter(user -> user.getFcmToken() != null && !user.getFcmToken().isBlank())
                    .toList();

            for (User user : users) {
                sendPushToUser(user.getEmail(), title, message);
            }

            logNotification(null, TYPE_BROADCAST, title + ": " + message, CHANNEL_PUSH, true, null);
        } catch (Exception e) {
            logNotification(null, TYPE_BROADCAST, title + ": " + message, CHANNEL_PUSH, false, e.getMessage());
            System.err.println("Error sending FCM broadcast: " + e.getMessage());
        }
    }

    /**
     * Log notification to database for audit and history.
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
            System.err.println("Error logging FCM notification: " + e.getMessage());
        }
    }
}

