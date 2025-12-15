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

    @Enumerated(EnumType.STRING)
    private com.example.disastermanagement.model.enums.AlertType alertType;

    @Enumerated(EnumType.STRING)
    private com.example.disastermanagement.model.enums.AlertSeverity severity;

    private String location;
    private String message;

    @Enumerated(EnumType.STRING)
    private com.example.disastermanagement.model.enums.AlertSource source;

    private boolean acknowledged = false;

    // Keeping lat/long for geospatial features
    private double latitude;
    private double longitude;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
