package com.example.disastermanagement.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sos_requests")
public class SosRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;          // user sending SOS
    private String disasterType;   // fire, flood, accident, etc.
    private String message;        // optional description
    private double latitude;
    private double longitude;

    private LocalDateTime createdAt;

    // Constructors
    public SosRequest() {
        this.createdAt = LocalDateTime.now();
    }

    public SosRequest(Long id, String email, String disasterType, String message, double latitude, double longitude, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.disasterType = disasterType;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisasterType() {
        return disasterType;
    }

    public void setDisasterType(String disasterType) {
        this.disasterType = disasterType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder
    public static SosRequestBuilder builder() {
        return new SosRequestBuilder();
    }

    public static class SosRequestBuilder {
        private Long id;
        private String email;
        private String disasterType;
        private String message;
        private double latitude;
        private double longitude;
        private LocalDateTime createdAt;

        public SosRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SosRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public SosRequestBuilder disasterType(String disasterType) {
            this.disasterType = disasterType;
            return this;
        }

        public SosRequestBuilder message(String message) {
            this.message = message;
            return this;
        }

        public SosRequestBuilder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public SosRequestBuilder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public SosRequestBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SosRequest build() {
            return new SosRequest(id, email, disasterType, message, latitude, longitude, createdAt != null ? createdAt : LocalDateTime.now());
        }
    }
}
