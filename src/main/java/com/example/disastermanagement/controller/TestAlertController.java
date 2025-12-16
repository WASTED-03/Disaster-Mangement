package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.Alert;
import com.example.disastermanagement.model.enums.AlertSeverity;
import com.example.disastermanagement.model.enums.AlertSource;
import com.example.disastermanagement.model.enums.AlertType;
import com.example.disastermanagement.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/alert")
public class TestAlertController {

    private final AlertService alertService;

    public TestAlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> testAlert(@RequestBody TestAlertRequest request) {
        try {
            Alert alert = new Alert();
            // Handle "type" mapping to AlertType
            if (request.type != null) {
                try {
                    alert.setAlertType(AlertType.valueOf(request.type.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid AlertType: " + request.type));
                }
            }

            if (request.severity != null) {
                try {
                    alert.setSeverity(AlertSeverity.valueOf(request.severity.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid Severity: " + request.severity));
                }
            }

            alert.setLocation(request.location);
            alert.setMessage(request.message);
            alert.setSource(AlertSource.MANUAL);

            Alert created = alertService.createAlert(alert);
            return ResponseEntity.ok(Map.of(
                    "message", "Test alert created successfully",
                    "alertId", created.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    public static class TestAlertRequest {
        public String type;
        public String severity;
        public String location;
        public String message;
    }
}
