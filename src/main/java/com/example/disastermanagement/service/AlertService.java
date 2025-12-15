package com.example.disastermanagement.service;

import com.example.disastermanagement.model.Alert;
import com.example.disastermanagement.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double DEFAULT_RADIUS_KM = 50.0; // Default radius for "near me" alerts

    private final AlertRepository repository;
    private final com.example.disastermanagement.service.notification.WebSocketNotificationService webSocketNotificationService;

    public AlertService(AlertRepository repository,
            com.example.disastermanagement.service.notification.WebSocketNotificationService webSocketNotificationService) {
        this.repository = repository;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    public Alert createAlert(Alert alert) {
        if (alert.getAlertType() == null) {
            throw new IllegalArgumentException("Alert Type is required");
        }
        if (alert.getSeverity() == null) {
            throw new IllegalArgumentException("Severity is required");
        }
        if (!StringUtils.hasText(alert.getMessage())) {
            throw new IllegalArgumentException("Message is required");
        }

        if (alert.getTimestamp() == null) {
            alert.setTimestamp(LocalDateTime.now());
        }

        // 1. Save to DB
        Alert savedAlert = repository.save(alert);

        // 2. Broadcast via WebSocket
        broadcastAlert(savedAlert);

        return savedAlert;
    }

    private void broadcastAlert(Alert alert) {
        try {
            // Construct JSON message matching the format used in WeatherScheduler
            // {"type": "WEATHER_ALERT", "alertType": "...", "severity": "...", "location":
            // "...", "message": "...", "timestamp": "..."}
            String jsonMessage = String.format(
                    "{\"type\": \"ALERT\", \"alertType\": \"%s\", \"severity\": \"%s\", \"location\": \"%s\", \"message\": \"%s\", \"timestamp\": \"%s\", \"source\": \"%s\", \"id\": %d}",
                    alert.getAlertType(),
                    alert.getSeverity(),
                    alert.getLocation() != null ? alert.getLocation() : "Unknown",
                    alert.getMessage(),
                    alert.getTimestamp().toString(),
                    alert.getSource(),
                    alert.getId());

            // Broadcast to location-specific topic if location is available
            webSocketNotificationService.notifyAdmins(jsonMessage, alert.getLocation());
        } catch (Exception e) {
            System.err.println("Error broadcasting alert: " + e.getMessage());
        }
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
                            alert.getLatitude(), alert.getLongitude());
                    return distance <= radiusKm;
                })
                .sorted((a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp())) // Most recent first
                .collect(Collectors.toList());
    }

    /**
     * Get alerts with filtering and pagination.
     */
    public org.springframework.data.domain.Page<Alert> getAlerts(
            com.example.disastermanagement.dto.AlertFilterCriteria criteria,
            org.springframework.data.domain.Pageable pageable) {
        return repository.findAll((root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (criteria.getAlertType() != null) {
                predicates.add(cb.equal(root.get("alertType"), criteria.getAlertType()));
            }
            if (criteria.getSeverity() != null) {
                predicates.add(cb.equal(root.get("severity"), criteria.getSeverity()));
            }
            if (StringUtils.hasText(criteria.getLocation())) {
                predicates
                        .add(cb.like(cb.lower(root.get("location")), "%" + criteria.getLocation().toLowerCase() + "%"));
            }
            if (criteria.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), criteria.getEndDate()));
            }
            if (criteria.getAcknowledged() != null) {
                predicates.add(cb.equal(root.get("acknowledged"), criteria.getAcknowledged()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
    }

    /**
     * Acknowledge an alert.
     */
    public Alert acknowledgeAlert(Long alertId) {
        Alert alert = repository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with id: " + alertId));

        alert.setAcknowledged(true);
        return repository.save(alert);
    }

    /**
     * Calculate distance between two points using Haversine formula
     * 
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
