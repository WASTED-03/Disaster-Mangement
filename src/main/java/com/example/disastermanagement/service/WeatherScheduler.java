package com.example.disastermanagement.service;

import com.example.disastermanagement.model.WeatherAlert;
import com.example.disastermanagement.model.WeatherData;
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

    // Hardcoded location: Bengaluru, India
    private static final double BENGALURU_LAT = 12.97;
    private static final double BENGALURU_LON = 77.59;
    private static final String LOCATION_NAME = "Bengaluru";

    public WeatherScheduler(WeatherAlertService weatherAlertService, 
                           AlertRulesService alertRulesService,
                           WeatherAlertRepository weatherAlertRepository) {
        this.weatherAlertService = weatherAlertService;
        this.alertRulesService = alertRulesService;
        this.weatherAlertRepository = weatherAlertRepository;
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
                for (String alertType : alerts) {
                    System.out.println("  - " + alertType);
                    
                    // Convert alert string to WeatherAlert entity and save
                    WeatherAlert weatherAlert = createWeatherAlert(alertType, weatherData, LOCATION_NAME);
                    weatherAlertRepository.save(weatherAlert);
                    savedCount++;
                }
                
                System.out.println("Weather conditions: " + 
                        "Temp=" + (weatherData.getTemperature() != null ? 
                                String.format("%.1f°C", weatherData.getTemperature() - 273.15) : "N/A") +
                        ", Humidity=" + (weatherData.getHumidity() != null ? weatherData.getHumidity() + "%" : "N/A") +
                        ", Condition=" + (weatherData.getCondition() != null ? weatherData.getCondition() : "N/A"));
                
                String saveTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                System.out.println("💾 Saved " + savedCount + " new alert(s) at " + saveTimestamp);
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
}

