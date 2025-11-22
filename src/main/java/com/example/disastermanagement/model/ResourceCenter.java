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
public class ResourceCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // SHELTER, HOSPITAL, FOOD, WATER, MEDICAL, FIRE_STATION, POLICE_STATION
    private String address;
    private double latitude;
    private double longitude;
    private String contactNumber;
    
    private Integer capacity; // Optional
    private Integer currentAvailability; // Optional
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

