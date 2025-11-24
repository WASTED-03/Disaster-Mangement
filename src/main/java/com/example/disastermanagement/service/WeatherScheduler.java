package com.example.disastermanagement.service;

import com.example.disastermanagement.model.User;
import com.example.disastermanagement.model.UserAlert;
import com.example.disastermanagement.model.WeatherAlert;
import com.example.disastermanagement.model.WeatherData;
import com.example.disastermanagement.repository.UserAlertRepository;
import com.example.disastermanagement.repository.UserRepository;
import com.example.disastermanagement.repository.WeatherAlertRepository;
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
    private final WeatherAlertRepository weatherAlertRepository;
    private final UserRepository userRepository;
    private final UserAlertRepository userAlertRepository;

    // Hardcoded location: Bengaluru, India
    private static final double BENGALURU_LAT = 12.97;
    private static final double BENGALURU_LON = 77.59;
    private static final String LOCATION_NAME = "Bengaluru";
    
    // Distance threshold for user alerts (in kilometers)
    private static final double ALERT_DISTANCE_THRESHOLD_KM = 20.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    public WeatherScheduler(WeatherAlertService weatherAlertService, 
                           AlertRulesService alertRulesService,
                           WeatherAlertRepository weatherAlertRepository,
                           UserRepository userRepository,
                           UserAlertRepository userAlertRepository) {
        this.weatherAlertService = weatherAlertService;
        this.alertRulesService = alertRulesService;
        this.weatherAlertRepository = weatherAlertRepository;
        this.userRepository = userRepository;
        this.userAlertRepository = userAlertRepository;
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
                for (String alertType : alerts) {
                    System.out.println("  - " + alertType);
                    
                    // Convert alert string to WeatherAlert entity and save
                    WeatherAlert weatherAlert = createWeatherAlert(alertType, weatherData, LOCATION_NAME);
                    WeatherAlert savedAlert = weatherAlertRepository.save(weatherAlert);
                    savedCount++;
                    
                    // STEP 10E: Match alert with nearby users and save to UserAlert
                    userAlertCount += matchAlertWithUsers(savedAlert);
                }
                
                System.out.println("Weather conditions: " + 
                        "Temp=" + (weatherData.getTemperature() != null ? 
                                String.format("%.1f°C", weatherData.getTemperature() - 273.15) : "N/A") +
                        ", Humidity=" + (weatherData.getHumidity() != null ? weatherData.getHumidity() + "%" : "N/A") +
                        ", Condition=" + (weatherData.getCondition() != null ? weatherData.getCondition() : "N/A"));
                
                String saveTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                System.out.println("💾 Saved " + savedCount + " new alert(s) at " + saveTimestamp);
                if (userAlertCount > 0) {
                    System.out.println("👤 Created " + userAlertCount + " user-specific alert(s) for nearby users");
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
     * Helper method to create a WeatherAlert entity from alert type and weather data.
     * 
     * @param alertType The type of alert (e.g., "FLOOD_RISK_HIGH", "CYCLONE_WARNING", "WILDFIRE_RISK_HIGH")
     * @param weatherData The weather data that triggered the alert
     * @param locationName The name of the location
     * @return WeatherAlert entity ready to be saved
     */
    private WeatherAlert createWeatherAlert(String alertType, WeatherData weatherData, String locationName) {
        // Determine alert type and severity from alert string
        String type = extractAlertType(alertType);
        String severity = extractSeverity(alertType);
        String message = generateAlertMessage(alertType, locationName, weatherData);
        
        return WeatherAlert.builder()
                .message(message)
                .type(type)
                .severity(severity)
                .timestamp(LocalDateTime.now())
                .latitude(BENGALURU_LAT)
                .longitude(BENGALURU_LON)
                .build();
    }

    /**
     * Extract alert type from alert string.
     */
    private String extractAlertType(String alertType) {
        if (alertType.contains("FLOOD")) {
            return "flood";
        } else if (alertType.contains("CYCLONE") || alertType.contains("STORM")) {
            return "storm";
        } else if (alertType.contains("WILDFIRE")) {
            return "wildfire_risk";
        } else {
            return "other";
        }
    }

    /**
     * Extract severity from alert string.
     */
    private String extractSeverity(String alertType) {
        if (alertType.contains("CRITICAL")) {
            return "critical";
        } else if (alertType.contains("HIGH") || alertType.contains("WARNING")) {
            return "high";
        } else if (alertType.contains("MEDIUM")) {
            return "medium";
        } else {
            return "low";
        }
    }

    /**
     * Generate a human-readable alert message.
     */
    private String generateAlertMessage(String alertType, String locationName, WeatherData weatherData) {
        String baseMessage = alertType.replace("_", " ") + " detected at " + locationName;
        
        if (weatherData != null) {
            String temp = weatherData.getTemperature() != null ? 
                    String.format("%.1f°C", weatherData.getTemperature() - 273.15) : "N/A";
            String condition = weatherData.getCondition() != null ? weatherData.getCondition() : "N/A";
            baseMessage += ". Current conditions: " + condition + ", Temperature: " + temp;
        }
        
        return baseMessage;
    }

    /**
     * STEP 10E: Match weather alert with nearby users and create UserAlert entries.
     * 
     * For each user with location data, calculates distance from alert location.
     * If distance < 20km, creates a UserAlert entry for that user.
     * 
     * @param weatherAlert The weather alert to match with users
     * @return Number of user alerts created
     */
    private int matchAlertWithUsers(WeatherAlert weatherAlert) {
        int userAlertCount = 0;
        
        try {
            // Get all users from database
            List<User> users = userRepository.findAll();
            
            for (User user : users) {
                // Skip users without location data
                if (user.getLatitude() == null || user.getLongitude() == null) {
                    continue;
                }
                
                // Calculate distance between alert location and user location
                double distance = calculateDistance(
                        weatherAlert.getLatitude(), weatherAlert.getLongitude(),
                        user.getLatitude(), user.getLongitude()
                );
                
                // If user is within 20km of alert, create UserAlert
                if (distance < ALERT_DISTANCE_THRESHOLD_KM) {
                    UserAlert userAlert = UserAlert.builder()
                            .userEmail(user.getEmail())
                            .alertMessage(weatherAlert.getMessage())
                            .type(weatherAlert.getType())
                            .severity(weatherAlert.getSeverity())
                            .timestamp(LocalDateTime.now())
                            .build();
                    
                    userAlertRepository.save(userAlert);
                    userAlertCount++;
                    
                    // Optionally: Send email/SMS notification here in the future
                }
            }
        } catch (Exception e) {
            System.err.println("[WEATHER SCHEDULER] Error matching alert with users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return userAlertCount;
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

