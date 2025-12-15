package com.example.disastermanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/alert")
public class AlertTestController {

    private final com.example.disastermanagement.service.AlertService alertService;

    public AlertTestController(com.example.disastermanagement.service.AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> testAlert() {
        // Create a test alert
        com.example.disastermanagement.model.Alert alert = com.example.disastermanagement.model.Alert.builder()
                .alertType(com.example.disastermanagement.model.enums.AlertType.TEST)
                .severity(com.example.disastermanagement.model.enums.AlertSeverity.LOW)
                .location("Test Location")
                .message("System working!")
                .source(com.example.disastermanagement.model.enums.AlertSource.MANUAL)
                .timestamp(LocalDateTime.now())
                .build();

        // Save and broadcast via AlertService
        alertService.createAlert(alert);

        return ResponseEntity.ok(Map.of("status", "Test alert created and broadcasted"));
    }
}
