package com.example.disastermanagement.service;

import com.example.disastermanagement.model.SosRequest;
import com.example.disastermanagement.model.User;
import com.example.disastermanagement.model.UserAlert;
import com.example.disastermanagement.model.WeatherData;
import com.example.disastermanagement.repository.SosRepository;
import com.example.disastermanagement.repository.UserAlertRepository;
import com.example.disastermanagement.repository.UserRepository;
import com.example.disastermanagement.service.notification.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Scheduled service for automatic weather monitoring and alert generation.
 * Runs periodically to check weather conditions and generate alerts.
 */
@Service
public class WeatherScheduler {

    private final WeatherAlertService weatherAlertService;
    private final AlertRulesService alertRulesService;
    private final AlertService alertService;
    private final UserRepository userRepository;
    private final UserAlertRepository userAlertRepository;
    private final SosRepository sosRepository;
    private final NotificationService notificationService;

    // Hardcoded location: Bengaluru, India
    private static final double BENGALURU_LAT = 12.97;
    private static final double BENGALURU_LON = 77.59;
    private static final String LOCATION_NAME = "Bengaluru";

    // Distance threshold for user alerts (in kilometers)
    private static final double ALERT_DISTANCE_THRESHOLD_KM = 20.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    // Severity levels for auto-SOS trigger
    private static final String HIGH_SEVERITY = "high";
    private static final String CRITICAL_SEVERITY = "critical";

    // Cooldown period for auto-SOS (30 minutes in minutes)
    private static final int AUTO_SOS_COOLDOWN_MINUTES = 30;

    public WeatherScheduler(WeatherAlertService weatherAlertService,
            AlertRulesService alertRulesService,
            AlertService alertService,
            UserRepository userRepository,
            UserAlertRepository userAlertRepository,
            SosRepository sosRepository,
            NotificationService notificationService) {
        this.weatherAlertService = weatherAlertService;
        this.alertRulesService = alertRulesService;
        this.alertService = alertService;
        this.userRepository = userRepository;
        this.userAlertRepository = userAlertRepository;
        this.sosRepository = sosRepository;
        this.notificationService = notificationService;
    }

    /**
     * Scheduled task that runs every 10 minutes.
     * Fetches weather data, generates alerts, and logs the results.
     * fixedRate = 600000 milliseconds (10 minutes)
     */
    @Scheduled(fixedRate = 600000)
    public void checkWeatherAndGenerateAlerts() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("\n=== [WEATHER SCHEDULER] Starting scheduled check at " + timestamp + " ===");

        try {
            // Step 1: Fetch weather data for Bengaluru
            System.out.println("[WEATHER SCHEDULER] Fetching weather for " + LOCATION_NAME +
                    " (lat=" + BENGALURU_LAT + ", lon=" + BENGALURU_LON + ")");

            WeatherData weatherData = weatherAlertService.fetchWeather(BENGALURU_LAT, BENGALURU_LON);

            // Step 2: Generate alerts using alert rules
            List<String> alerts = alertRulesService.analyzeAlerts(weatherData);

            // Step 3: Save alerts to database and print
            if (alerts != null && !alerts.isEmpty()) {
                System.out.println("\n[WEATHER ALERT] ⚠️  THREATS DETECTED at " + LOCATION_NAME + ":");

                int savedCount = 0;
                int userAlertCount = 0;
                int autoSosCount = 0;
                for (String alertTypeStr : alerts) {
                    System.out.println("  - " + alertTypeStr);

                    // Convert alert string to Alert entity and save via AlertService
                    // This handles saving and broadcasting
                    com.example.disastermanagement.model.Alert alert = createAlert(alertTypeStr, weatherData,
                            LOCATION_NAME);
                    com.example.disastermanagement.model.Alert savedAlert = alertService.createAlert(alert);
                    savedCount++;

                    // STEP 10E: Match alert with nearby users and save to UserAlert
                    // STEP 11B: Also create auto-SOS for HIGH severity alerts
                    int[] results = matchAlertWithUsers(savedAlert);
                    userAlertCount += results[0];
                    autoSosCount += results[1];
                }

                System.out.println("Weather conditions: " +
                        "Temp="
                        + (weatherData.getTemperature() != null
                                ? String.format("%.1f°C", weatherData.getTemperature() - 273.15)
                                : "N/A")
                        +
                        ", Humidity=" + (weatherData.getHumidity() != null ? weatherData.getHumidity() + "%" : "N/A") +
                        ", Condition=" + (weatherData.getCondition() != null ? weatherData.getCondition() : "N/A"));

                String saveTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                System.out.println("💾 Saved " + savedCount + " new alert(s) at " + saveTimestamp);
                if (userAlertCount > 0) {
                    System.out.println("👤 Created " + userAlertCount + " user-specific alert(s) for nearby users");
                }
                if (autoSosCount > 0) {
                    System.out
                            .println("🚨 Auto-triggered " + autoSosCount + " SOS request(s) for high-severity alerts");
                }
            } else {
                System.out.println("[WEATHER ALERT] ✅ No threats detected at " + LOCATION_NAME +
                        ". Weather conditions are normal.");
            }

            System.out.println("=== [WEATHER SCHEDULER] Check completed ===\n");

        } catch (Exception e) {
            System.err.println("[WEATHER SCHEDULER] ❌ ERROR during scheduled check: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create an Alert entity from alert type and weather data.
     */
    private com.example.disastermanagement.model.Alert createAlert(String alertTypeStr, WeatherData weatherData,
            String locationName) {
        // Determine alert type and severity from alert string
        com.example.disastermanagement.model.enums.AlertType type = extractAlertType(alertTypeStr);
        com.example.disastermanagement.model.enums.AlertSeverity severity = extractSeverity(alertTypeStr);
        String message = generateAlertMessage(alertTypeStr, locationName, weatherData);

        return com.example.disastermanagement.model.Alert.builder()
                .message(message)
                .alertType(type)
                .severity(severity)
                .location(locationName)
                .source(com.example.disastermanagement.model.enums.AlertSource.RULE_ENGINE)
                .timestamp(LocalDateTime.now())
                .latitude(BENGALURU_LAT)
                .longitude(BENGALURU_LON)
                .build();
    }

    /**
     * Extract alert type from alert string.
     */
    private com.example.disastermanagement.model.enums.AlertType extractAlertType(String alertType) {
        if (alertType.contains("FLOOD")) {
            return com.example.disastermanagement.model.enums.AlertType.FLOOD;
        } else if (alertType.contains("CYCLONE") || alertType.contains("STORM")) {
            return com.example.disastermanagement.model.enums.AlertType.STORM;
        } else if (alertType.contains("WILDFIRE")) {
            return com.example.disastermanagement.model.enums.AlertType.WILDFIRE;
        } else {
            return com.example.disastermanagement.model.enums.AlertType.OTHER;
        }
    }

    /**
     * Extract severity from alert string.
     */
    private com.example.disastermanagement.model.enums.AlertSeverity extractSeverity(String alertType) {
        if (alertType.contains("CRITICAL")) {
            return com.example.disastermanagement.model.enums.AlertSeverity.CRITICAL;
        } else if (alertType.contains("HIGH") || alertType.contains("WARNING")) {
            return com.example.disastermanagement.model.enums.AlertSeverity.HIGH;
        } else if (alertType.contains("MEDIUM")) {
            return com.example.disastermanagement.model.enums.AlertSeverity.MEDIUM;
        } else {
            return com.example.disastermanagement.model.enums.AlertSeverity.LOW;
        }
    }

    /**
     * Generate a human-readable alert message.
     */
    private String generateAlertMessage(String alertType, String locationName, WeatherData weatherData) {
        String baseMessage = alertType.replace("_", " ") + " detected at " + locationName;

        if (weatherData != null) {
            String temp = weatherData.getTemperature() != null
                    ? String.format("%.1f°C", weatherData.getTemperature() - 273.15)
                    : "N/A";
            String condition = weatherData.getCondition() != null ? weatherData.getCondition() : "N/A";
            baseMessage += ". Current conditions: " + condition + ", Temperature: " + temp;
        }

        return baseMessage;
    }

    /**
     * Match alert with nearby users and create UserAlert entries.
     */
    private int[] matchAlertWithUsers(com.example.disastermanagement.model.Alert alert) {
        int userAlertCount = 0;
        int autoSosCount = 0;

        try {
            // Get all users from database
            List<User> users = userRepository.findAll();

            // Check if alert severity is HIGH (for auto-SOS)
            // Using Enum comparison
            boolean isHighSeverity = alert
                    .getSeverity() == com.example.disastermanagement.model.enums.AlertSeverity.HIGH ||
                    alert.getSeverity() == com.example.disastermanagement.model.enums.AlertSeverity.CRITICAL;

            // Note: Admin notification is now handled by AlertService.createAlert()

            for (User user : users) {
                // Skip users without location data
                if (user.getLatitude() == null || user.getLongitude() == null) {
                    continue;
                }

                // Calculate distance between alert location and user location
                double distance = calculateDistance(
                        alert.getLatitude(), alert.getLongitude(),
                        user.getLatitude(), user.getLongitude());

                // If user is within 20km of alert, create UserAlert
                if (distance < ALERT_DISTANCE_THRESHOLD_KM) {
                    // Create UserAlert entry
                    UserAlert userAlert = UserAlert.builder()
                            .userEmail(user.getEmail())
                            .alertMessage(alert.getMessage())
                            .type(alert.getAlertType().toString())
                            .severity(alert.getSeverity().toString())
                            .timestamp(LocalDateTime.now())
                            .build();

                    userAlertRepository.save(userAlert);
                    userAlertCount++;

                    // Send real-time notification to user about weather alert
                    try {
                        String userMessage = String.format("Severe Weather Warning! %s - %s",
                                alert.getAlertType(), alert.getMessage());
                        notificationService.notifyUser(user.getEmail(), userMessage);
                    } catch (Exception e) {
                        System.err.println("Error sending weather alert notification to user: " + e.getMessage());
                    }

                    // Auto-create SOS for HIGH severity alerts
                    if (isHighSeverity) {
                        // Check if user already has an auto-SOS in the last 30 minutes
                        LocalDateTime cooldownThreshold = LocalDateTime.now().minusMinutes(AUTO_SOS_COOLDOWN_MINUTES);
                        List<SosRequest> recentAutoSos = sosRepository
                                .findByUserEmailAndAutoGeneratedTrueAndTimestampAfter(
                                        user.getEmail(), cooldownThreshold);

                        if (recentAutoSos.isEmpty()) {
                            // No recent auto-SOS - create new one
                            SosRequest autoSos = SosRequest.builder()
                                    .userEmail(user.getEmail())
                                    .latitude(user.getLatitude())
                                    .longitude(user.getLongitude())
                                    .message("AUTO-SOS: Severe weather danger in your area")
                                    .type(alert.getAlertType().toString())
                                    .status("PENDING")
                                    .timestamp(LocalDateTime.now())
                                    .autoGenerated(true)
                                    .build();

                            sosRepository.save(autoSos);
                            autoSosCount++;

                            System.out.println("  🚨 Auto-SOS created for user: " + user.getEmail() +
                                    " (Severity: " + alert.getSeverity() + ")");
                        } else {
                            // Cooldown active - skip creating duplicate auto-SOS
                            System.out.println("  ⏸️  Auto-SOS skipped for user: " + user.getEmail() +
                                    " (Cooldown active - last auto-SOS was " +
                                    recentAutoSos.get(0).getTimestamp() + ")");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[WEATHER SCHEDULER] Error matching alert with users: " + e.getMessage());
            e.printStackTrace();
        }

        return new int[] { userAlertCount, autoSosCount };
    }

    /**
     * Calculate distance between two points using Haversine formula.
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
