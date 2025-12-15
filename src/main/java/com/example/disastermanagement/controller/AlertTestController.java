package com.example.disastermanagement.controller;

import com.example.disastermanagement.service.notification.WebSocketNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/alert")
public class AlertTestController {

    private final WebSocketNotificationService webSocketNotificationService;

    public AlertTestController(WebSocketNotificationService webSocketNotificationService) {
        this.webSocketNotificationService = webSocketNotificationService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> testAlert() {
        // Create the JSON message structure as requested
        // {
        // "type": "TEST_ALERT",
        // "message": "System working!",
        // "time": "2025-11-21T12:40:00"
        // }

        // We need to send a JSON string because the service takes a String message.
        // Ideally the service should take an object, but for now we'll construct a JSON
        // string
        // or if the service sends the string as is, we can send the JSON string.

        // However, looking at WebSocketNotificationService.notifyAdmins, it sends the
        // string directly.
        // If the client expects JSON, we should send a JSON string.

        String jsonMessage = String.format(
                "{\"type\": \"TEST_ALERT\", \"message\": \"System working!\", \"time\": \"%s\"}",
                LocalDateTime.now().toString());

        webSocketNotificationService.notifyAdmins(jsonMessage);

        return ResponseEntity.ok(Map.of("status", "Test alert sent"));
    }
}
