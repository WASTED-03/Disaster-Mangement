package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.Alert;
import com.example.disastermanagement.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Alert>> getLatestAlerts() {
        return ResponseEntity.ok(alertService.getLatestAlerts());
    }

    @GetMapping("/near")
    public ResponseEntity<List<Alert>> getAlertsNear(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false) Double radius) {
        
        List<Alert> alerts;
        if (radius != null && radius > 0) {
            alerts = alertService.getAlertsNear(lat, lng, radius);
        } else {
            alerts = alertService.getAlertsNear(lat, lng);
        }
        
        return ResponseEntity.ok(alerts);
    }
}

