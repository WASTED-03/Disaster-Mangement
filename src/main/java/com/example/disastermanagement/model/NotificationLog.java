package com.example.disastermanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity to store notification logs for audit and history purposes.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String recipientEmail; // null for broadcast notifications

    @Column(nullable = false, length = 50)
    private String notificationType; // USER, ADMIN, BROADCAST

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false, length = 50)
    private String channel; // WEBSOCKET, EMAIL, SMS, PUSH

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @Builder.Default
    private Boolean sent = true; // false if notification failed

    @Column(length = 500)
    private String errorMessage; // null if sent successfully

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}

