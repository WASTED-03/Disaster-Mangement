package com.example.disastermanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminPanelController {

    @GetMapping("/panel")
    public ResponseEntity<?> panel() {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "status", "ok",
                        "message", "Welcome to the admin panel"
                )
        );
    }
}


