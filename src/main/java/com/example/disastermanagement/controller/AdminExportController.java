package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.SosRequest;
import com.example.disastermanagement.model.User;
import com.example.disastermanagement.repository.SosRepository;
import com.example.disastermanagement.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/export")
public class AdminExportController {

    private final SosRepository sosRepository;
    private final UserRepository userRepository;

    public AdminExportController(SosRepository sosRepository, UserRepository userRepository) {
        this.sosRepository = sosRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/sos")
    public ResponseEntity<?> exportSos() {
        List<SosRequest> sos = sosRepository.findAll();
        return ResponseEntity.ok(Map.of(
                "status", "pending",
                "message", "Export feature not implemented yet",
                "data", sos
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> exportUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(Map.of(
                "status", "pending",
                "message", "Export feature not implemented yet",
                "data", users
        ));
    }
}


