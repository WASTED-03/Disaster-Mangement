package com.example.disastermanagement.service;

import com.example.disastermanagement.model.WeatherData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for applying alert rules to weather data.
 * Analyzes WeatherData and determines which disaster alerts should be triggered.
 */
@Service
public class AlertRulesService {

    // Temperature thresholds
    private static final double FLOOD_MAX_TEMP_C = 30.0;  // Convert from Kelvin: 30°C = 303.15K
    private static final double WILDFIRE_MIN_TEMP_C = 35.0;  // Convert from Kelvin: 35°C = 308.15K
    
    // Humidity thresholds
    private static final double FLOOD_MIN_HUMIDITY = 85.0;
    private static final double WILDFIRE_MAX_HUMIDITY = 25.0;
    
    // Pressure thresholds
    private static final double FLOOD_MAX_PRESSURE = 1000.0;  // hPa
    private static final double CYCLONE_MAX_PRESSURE = 990.0;  // hPa
    
    // Wind speed thresholds (m/s)
    private static final double CYCLONE_MIN_WIND_SPEED = 20.0;  // m/s (72 km/h)
    private static final double WILDFIRE_MIN_WIND_SPEED = 10.0;  // m/s

    /**
     * Analyzes weather data and returns a list of alerts based on predefined rules.
     * 
     * @param weatherData The parsed weather data to analyze
     * @return List of alert strings (e.g., "FLOOD_RISK_HIGH", "CYCLONE_WARNING", "WILDFIRE_RISK_HIGH")
     */
    public List<String> analyzeAlerts(WeatherData weatherData) {
        List<String> alerts = new ArrayList<>();
        
        if (weatherData == null) {
            return alerts; // Return empty list if no data
        }
        
        // Convert temperature from Kelvin to Celsius for easier comparison
        Double tempCelsius = weatherData.getTemperature() != null ? 
                weatherData.getTemperature() - 273.15 : null;
        
        // Check for Flood Alert
        if (checkFloodConditions(weatherData, tempCelsius)) {
            alerts.add("FLOOD_RISK_HIGH");
        }
        
        // Check for Cyclone/Storm Alert
        if (checkCycloneConditions(weatherData)) {
            alerts.add("CYCLONE_WARNING");
        }
        
        // Check for Wildfire Alert
        if (checkWildfireConditions(weatherData, tempCelsius)) {
            alerts.add("WILDFIRE_RISK_HIGH");
        }
        
        return alerts;
    }

    /**
     * B.1. Flood Alert Rules
     * 
     * Trigger alert if:
     * - Temperature < 30°C
     * - Humidity > 85%
     * - Pressure < 1000 hPa
     * - Condition contains "Rain" or "Thunderstorm"
     */
    private boolean checkFloodConditions(WeatherData weatherData, Double tempCelsius) {
        // Check temperature
        if (tempCelsius == null || tempCelsius >= FLOOD_MAX_TEMP_C) {
            return false;
        }
        
        // Check humidity
        if (weatherData.getHumidity() == null || weatherData.getHumidity() <= FLOOD_MIN_HUMIDITY) {
            return false;
        }
        
        // Check pressure
        if (weatherData.getPressure() == null || weatherData.getPressure() >= FLOOD_MAX_PRESSURE) {
            return false;
        }
        
        // Check condition for rain or thunderstorm
        String condition = weatherData.getCondition();
        if (condition == null) {
            return false;
        }
        
        String conditionUpper = condition.toUpperCase();
        boolean hasRainCondition = conditionUpper.contains("RAIN") || 
                                   conditionUpper.contains("THUNDERSTORM");
        
        return hasRainCondition;
    }

    /**
     * B.2. Cyclone / Storm Alert Rules
     * 
     * Trigger alert if:
     * - Wind speed > 20 m/s (72 km/h)
     * - Pressure < 990 hPa
     * - Condition contains "Storm", "Thunderstorm", "Hurricane"
     */
    private boolean checkCycloneConditions(WeatherData weatherData) {
        // Check wind speed
        if (weatherData.getWindSpeed() == null || weatherData.getWindSpeed() <= CYCLONE_MIN_WIND_SPEED) {
            return false;
        }
        
        // Check pressure
        if (weatherData.getPressure() == null || weatherData.getPressure() >= CYCLONE_MAX_PRESSURE) {
            return false;
        }
        
        // Check condition for storm-related weather
        String condition = weatherData.getCondition();
        if (condition == null) {
            return false;
        }
        
        String conditionUpper = condition.toUpperCase();
        boolean hasStormCondition = conditionUpper.contains("STORM") || 
                                    conditionUpper.contains("THUNDERSTORM") || 
                                    conditionUpper.contains("HURRICANE");
        
        return hasStormCondition;
    }

    /**
     * B.3. Wildfire Alert Rules
     * 
     * Trigger if:
     * - Temperature > 35°C
     * - Humidity < 25%
     * - Condition = "Clear" OR "Sunny"
     * - Wind speed > 10 m/s
     */
    private boolean checkWildfireConditions(WeatherData weatherData, Double tempCelsius) {
        // Check temperature
        if (tempCelsius == null || tempCelsius <= WILDFIRE_MIN_TEMP_C) {
            return false;
        }
        
        // Check humidity
        if (weatherData.getHumidity() == null || weatherData.getHumidity() >= WILDFIRE_MAX_HUMIDITY) {
            return false;
        }
        
        // Check condition for clear/sunny weather
        String condition = weatherData.getCondition();
        if (condition == null) {
            return false;
        }
        
        String conditionUpper = condition.toUpperCase();
        boolean hasClearCondition = conditionUpper.contains("CLEAR") || 
                                    conditionUpper.contains("SUNNY");
        
        if (!hasClearCondition) {
            return false;
        }
        
        // Check wind speed
        if (weatherData.getWindSpeed() == null || weatherData.getWindSpeed() <= WILDFIRE_MIN_WIND_SPEED) {
            return false;
        }
        
        return true;
    }
}

