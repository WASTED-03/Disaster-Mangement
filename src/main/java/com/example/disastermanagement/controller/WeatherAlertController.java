package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.WeatherData;
import com.example.disastermanagement.service.AlertRulesService;
import com.example.disastermanagement.service.WeatherAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for weather-based disaster alert APIs.
 * This controller exposes endpoints to fetch weather data and generate alerts.
 */
@RestController
@RequestMapping("/weather")
public class WeatherAlertController {

    private final WeatherAlertService weatherAlertService;
    private final AlertRulesService alertRulesService;

    public WeatherAlertController(WeatherAlertService weatherAlertService, 
                                  AlertRulesService alertRulesService) {
        this.weatherAlertService = weatherAlertService;
        this.alertRulesService = alertRulesService;
    }

    /**
     * B. Endpoint for fetching weather alerts based on location
     * 
     * This endpoint accepts latitude and longitude as query parameters,
     * fetches and parses weather data, applies alert rules, and returns
     * both weather data and alerts.
     * 
     * @param lat Latitude coordinate
     * @param lon Longitude coordinate
     * @return Response containing weather data and list of alerts
     */
    @GetMapping("/alert")
    public ResponseEntity<?> getWeatherAlert(
            @RequestParam double lat,
            @RequestParam double lon) {
        
        // Step 1: Fetch and parse weather data from OpenWeather API
        System.out.println("DEBUG: Fetching weather for lat=" + lat + ", lon=" + lon);
        WeatherData weatherData = null;
        try {
            weatherData = weatherAlertService.fetchWeather(lat, lon);
            System.out.println("DEBUG: Weather data fetched and parsed successfully");
        } catch (Exception e) {
            System.out.println("DEBUG: Error fetching weather: " + e.getMessage());
            throw e; // Re-throw to return error response
        }
        
        // Step 2: Analyze weather data using alert rules
        List<String> alerts = alertRulesService.analyzeAlerts(weatherData);
        System.out.println("DEBUG: Alerts generated: " + alerts);
        
        // Return response with weather data and alerts
        return ResponseEntity.ok(Map.of(
                "weather", weatherData,
                "alerts", alerts
        ));
    }
}

