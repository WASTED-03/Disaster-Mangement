package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.Alert;
import com.example.disastermanagement.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/alerts")
public class AdminAlertController {

    private final AlertService alertService;

    public AdminAlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAlert(@RequestBody Alert alert) {
        Alert created = alertService.createAlert(alert);
        return ResponseEntity.ok(Map.of(
                "message", "Alert created successfully",
                "alertId", created.getId()
        ));
    }
}

