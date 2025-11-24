package com.example.disastermanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity to store user-specific weather alerts.
 * This table stores alerts that are relevant to individual users based on their location.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_alert")
public class UserAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String userEmail;

    @Column(nullable = false, length = 500)
    private String alertMessage;

    @Column(nullable = false, length = 50)
    private String type; // flood, storm, wildfire_risk, etc.

    @Column(nullable = false, length = 20)
    private String severity; // low, medium, high, critical

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}

