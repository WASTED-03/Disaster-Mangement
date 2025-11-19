package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.service.DisasterReportService;
import com.example.disastermanagement.config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class DisasterReportController {

    private final DisasterReportService service;
    private final JwtUtil jwtUtil;

    public DisasterReportController(DisasterReportService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody DisasterReport report) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        report.setUserEmail(email);
        return ResponseEntity.ok(service.createReport(report));
    }

    @GetMapping("/my-reports")
    public ResponseEntity<List<DisasterReport>> myReports(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(service.getByUser(email));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DisasterReport>> all() {
        return ResponseEntity.ok(service.getAll());
    }
}
