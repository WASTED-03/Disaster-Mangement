package com.example.disastermanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private double latitude;
    private double longitude;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

