package com.example.disastermanagement.service;

import com.example.disastermanagement.model.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service for fetching weather data from OpenWeather API
 * and analyzing it to detect potential disaster conditions.
 */
@Service
public class WeatherAlertService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openweather.api.key:}")
    private String apiKey;

    @Value("${openweather.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;

    public WeatherAlertService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * A. Fetch weather data from OpenWeather API
     * 
     * This method builds the API URL using latitude, longitude, and API key,
     * makes an HTTP GET call, parses the JSON response, and returns WeatherData.
     * 
     * @param latitude  The latitude coordinate
     * @param longitude The longitude coordinate
     * @return WeatherData object with parsed weather parameters
     */
    public WeatherData fetchWeather(double latitude, double longitude) {
        // Build API URL with parameters
        String url = String.format("%s?lat=%.2f&lon=%.2f&appid=%s",
                apiUrl, latitude, longitude, apiKey);

        // Log the full URL being requested
        System.out.println("DEBUG: OpenWeather API URL: " + url);

        try {
            // Make HTTP GET request using RestTemplate
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // Log the raw JSON response
            System.out.println("DEBUG: Raw JSON response from OpenWeather: " + jsonResponse);

            // Parse JSON and extract weather parameters
            WeatherData weatherData = parseWeatherData(jsonResponse);

            System.out.println("DEBUG: Parsed weather data - Temperature: " + weatherData.getTemperature() +
                    ", Humidity: " + weatherData.getHumidity() +
                    ", Condition: " + weatherData.getCondition());

            return weatherData;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch weather data: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch weather data from OpenWeather API", e);
        }
    }

    /**
     * Parse JSON string from OpenWeather API and extract key weather parameters.
     * 
     * Extracts:
     * - Temperature from main.temp
     * - Humidity from main.humidity
     * - Pressure from main.pressure
     * - Wind Speed from wind.speed
     * - Weather Condition from weather[0].main
     * - Description from weather[0].description
     * 
     * @param jsonString Raw JSON string from OpenWeather API
     * @return WeatherData object with extracted parameters
     */
    private WeatherData parseWeatherData(String jsonString) {
        try {
            // Convert JSON string to JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Extract main object
            JsonNode mainNode = rootNode.get("main");
            JsonNode windNode = rootNode.get("wind");
            JsonNode weatherArray = rootNode.get("weather");

            // Extract temperature (in Kelvin)
            Double temperature = null;
            if (mainNode != null && mainNode.has("temp")) {
                temperature = mainNode.get("temp").asDouble();
            }

            // Extract humidity (percentage)
            Integer humidity = null;
            if (mainNode != null && mainNode.has("humidity")) {
                humidity = mainNode.get("humidity").asInt();
            }

            // Extract pressure (hPa)
            Double pressure = null;
            if (mainNode != null && mainNode.has("pressure")) {
                pressure = mainNode.get("pressure").asDouble();
            }

            // Extract wind speed (m/s)
            Double windSpeed = null;
            if (windNode != null && windNode.has("speed")) {
                windSpeed = windNode.get("speed").asDouble();
            }

            // Extract weather condition and description from weather array
            String condition = null;
            String description = null;
            if (weatherArray != null && weatherArray.isArray() && weatherArray.size() > 0) {
                JsonNode weatherNode = weatherArray.get(0);
                if (weatherNode.has("main")) {
                    condition = weatherNode.get("main").asText();
                }
                if (weatherNode.has("description")) {
                    description = weatherNode.get("description").asText();
                }
            }

            // Build and return WeatherData object
            return WeatherData.builder()
                    .temperature(temperature)
                    .humidity(humidity)
                    .windSpeed(windSpeed)
                    .pressure(pressure)
                    .condition(condition)
                    .description(description)
                    .build();

        } catch (Exception e) {
            System.err.println("ERROR: Failed to parse weather data: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse weather data from JSON", e);
        }
    }
}
