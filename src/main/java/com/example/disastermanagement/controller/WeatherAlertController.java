package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.WeatherAlert;
import com.example.disastermanagement.repository.WeatherAlertRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing weather alerts stored in the database.
 */
@RestController
@RequestMapping("/alerts")
public class WeatherAlertController {

    private final WeatherAlertRepository weatherAlertRepository;

    public WeatherAlertController(WeatherAlertRepository weatherAlertRepository) {
        this.weatherAlertRepository = weatherAlertRepository;
    }

    /**
     * A. Get all weather alerts (Admin only)
     * 
     * Returns all alerts from the database, ordered by timestamp descending.
     * 
     * @return List of all weather alerts
     */
    @GetMapping("/all")
    public ResponseEntity<List<WeatherAlert>> getAllAlerts() {
        List<WeatherAlert> alerts = weatherAlertRepository.findAllByOrderByTimestampDesc();
        return ResponseEntity.ok(alerts);
    }

    /**
     * B. Get recent weather alerts (User accessible)
     * 
     * Returns alerts from the last N hours, ordered by timestamp descending.
     * 
     * @param hours Number of hours to look back (default: 6)
     * @return List of recent weather alerts
     */
    @GetMapping("/recent")
    public ResponseEntity<List<WeatherAlert>> getRecentAlerts(
            @RequestParam(defaultValue = "6") int hours) {
        
        // Calculate timestamp for N hours ago
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        
        // Fetch alerts after the cutoff time
        List<WeatherAlert> alerts = weatherAlertRepository.findByTimestampAfterOrderByTimestampDesc(cutoffTime);
        
        return ResponseEntity.ok(alerts);
    }
}
