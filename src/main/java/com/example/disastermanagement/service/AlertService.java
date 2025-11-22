package com.example.disastermanagement.service;

import com.example.disastermanagement.model.Alert;
import com.example.disastermanagement.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private static final Set<String> ALLOWED_SEVERITIES = Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double DEFAULT_RADIUS_KM = 50.0; // Default radius for "near me" alerts

    private final AlertRepository repository;

    public AlertService(AlertRepository repository) {
        this.repository = repository;
    }

    public Alert createAlert(Alert alert) {
        if (!StringUtils.hasText(alert.getTitle())) {
            throw new IllegalArgumentException("Title is required");
        }
        if (!StringUtils.hasText(alert.getMessage())) {
            throw new IllegalArgumentException("Message is required");
        }
        if (!StringUtils.hasText(alert.getSeverity())) {
            throw new IllegalArgumentException("Severity is required");
        }

        String severity = alert.getSeverity().toUpperCase();
        if (!ALLOWED_SEVERITIES.contains(severity)) {
            throw new IllegalArgumentException("Severity must be LOW, MEDIUM, HIGH, or CRITICAL");
        }

        alert.setSeverity(severity);
        if (alert.getTimestamp() == null) {
            alert.setTimestamp(LocalDateTime.now());
        }

        return repository.save(alert);
    }

    public List<Alert> getLatestAlerts() {
        return repository.findTop20ByOrderByTimestampDesc();
    }

    public List<Alert> getAlertsNear(double latitude, double longitude) {
        return getAlertsNear(latitude, longitude, DEFAULT_RADIUS_KM);
    }

    public List<Alert> getAlertsNear(double latitude, double longitude, double radiusKm) {
        List<Alert> allAlerts = repository.findAll();
        
        return allAlerts.stream()
                .filter(alert -> {
                    double distance = calculateDistance(
                            latitude, longitude,
                            alert.getLatitude(), alert.getLongitude()
                    );
                    return distance <= radiusKm;
                })
                .sorted((a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp())) // Most recent first
                .collect(Collectors.toList());
    }

    /**
     * Calculate distance between two points using Haversine formula
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}

