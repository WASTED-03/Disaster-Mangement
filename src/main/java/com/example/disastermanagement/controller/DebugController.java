package com.example.disastermanagement.controller;

import com.example.disastermanagement.service.WeatherScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final WeatherScheduler weatherScheduler;

    public DebugController(WeatherScheduler weatherScheduler) {
        this.weatherScheduler = weatherScheduler;
    }

    @GetMapping("/public")
    public String publicTest() {
        return "Public OK";
    }

    @GetMapping("/private")
    public String privateTest() {
        return "Private OK";
    }

    /**
     * Test endpoint to manually trigger weather check and alert generation.
     * This is a temporary debug endpoint for testing purposes.
     * 
     * @return Success message
     */
    @GetMapping("/run-weather-check")
    public ResponseEntity<?> runWeatherCheck() {
        try {
            // Manually trigger the weather scheduler
            weatherScheduler.checkWeatherAndGenerateAlerts();
            return ResponseEntity.ok(Map.of(
                    "message", "Weather check completed successfully. Check console logs for details.",
                    "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error running weather check: " + e.getMessage(),
                    "status", "error"
            ));
        }
    }
}
