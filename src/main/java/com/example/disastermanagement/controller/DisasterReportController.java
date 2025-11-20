package com.example.disastermanagement.controller;

import com.example.disastermanagement.config.JwtUtil;
import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.service.DisasterReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        validate(report);
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        DisasterReport saved = service.createReport(report, email);
        return ResponseEntity.ok(Map.of(
                "message", "Report submitted",
                "reportId", saved.getId()
        ));
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

    private void validate(DisasterReport report) {
        if (!StringUtils.hasText(report.getType())) {
            throw new IllegalArgumentException("type is required");
        }
        if (!StringUtils.hasText(report.getDescription())) {
            throw new IllegalArgumentException("description is required");
        }
        if (report.getSeverity() == null) {
            throw new IllegalArgumentException("severity is required");
        }
    }
}
