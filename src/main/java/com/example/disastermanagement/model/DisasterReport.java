package com.example.disastermanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisasterReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;      // User who reported
    private String type;           // fire, flood, accident, etc.
    private String description;

    private double latitude;
    private double longitude;

    private String severity;       // low, medium, high
    private LocalDateTime timestamp;
}
