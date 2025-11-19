package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.SosRequest;
import com.example.disastermanagement.service.SosService;
import com.example.disastermanagement.config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sos")
public class SosController {

    private final SosService sosService;
    private final JwtUtil jwtUtil;

    public SosController(SosService sosService, JwtUtil jwtUtil) {
        this.sosService = sosService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSos(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SosRequest sos
    ) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        sos.setUserEmail(email);
        return ResponseEntity.ok(sosService.createSos(sos));
    }

    @GetMapping("/list")
    public ResponseEntity<List<SosRequest>> listAll() {
        return ResponseEntity.ok(sosService.getAll());
    }
}
