package com.example.disastermanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to hold extracted weather data from OpenWeather API response.
 * This class represents the parsed weather parameters needed for alert analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    
    private Double temperature;      // Temperature in Kelvin (from main.temp)
    private Integer humidity;        // Humidity percentage (from main.humidity)
    private Double windSpeed;        // Wind speed in m/s (from wind.speed)
    private Double pressure;         // Atmospheric pressure in hPa (from main.pressure)
    private String condition;        // Main weather condition (from weather[0].main)
    private String description;      // Weather description (from weather[0].description)
}

