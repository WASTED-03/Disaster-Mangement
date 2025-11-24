package com.example.disastermanagement.service;

import com.example.disastermanagement.model.WeatherData;
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

    // Hardcoded location: Bengaluru, India
    private static final double BENGALURU_LAT = 12.97;
    private static final double BENGALURU_LON = 77.59;
    private static final String LOCATION_NAME = "Bengaluru";

    public WeatherScheduler(WeatherAlertService weatherAlertService, 
                           AlertRulesService alertRulesService) {
        this.weatherAlertService = weatherAlertService;
        this.alertRulesService = alertRulesService;
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
            
            // Step 3: Print alerts or log "No threats detected"
            if (alerts != null && !alerts.isEmpty()) {
                System.out.println("\n[WEATHER ALERT] ⚠️  THREATS DETECTED at " + LOCATION_NAME + ":");
                for (String alert : alerts) {
                    System.out.println("  - " + alert);
                }
                System.out.println("Weather conditions: " + 
                        "Temp=" + (weatherData.getTemperature() != null ? 
                                String.format("%.1f°C", weatherData.getTemperature() - 273.15) : "N/A") +
                        ", Humidity=" + (weatherData.getHumidity() != null ? weatherData.getHumidity() + "%" : "N/A") +
                        ", Condition=" + (weatherData.getCondition() != null ? weatherData.getCondition() : "N/A"));
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
}

