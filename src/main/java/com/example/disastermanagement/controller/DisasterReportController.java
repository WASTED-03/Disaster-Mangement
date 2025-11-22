package com.example.disastermanagement.controller;

import com.example.disastermanagement.config.JwtUtil;
import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.model.ReportStatus;
import com.example.disastermanagement.service.DisasterReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
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

    @GetMapping("/my")
    public ResponseEntity<List<DisasterReport>> myReports(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        
        ReportStatus reportStatus = null;
        if (StringUtils.hasText(status)) {
            try {
                reportStatus = ReportStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status. Must be PENDING, APPROVED, REJECTED, or RESOLVED");
            }
        }
        
        return ResponseEntity.ok(service.getMyReports(email, reportStatus));
    }

    @GetMapping("/my/summary")
    public ResponseEntity<Map<String, Long>> mySummary(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(service.getSummary(email));
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editReport(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody DisasterReport updates) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        DisasterReport updated = service.editReport(id, email, updates);
        return ResponseEntity.ok(Map.of(
                "message", "Report updated successfully",
                "reportId", updated.getId()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        service.deleteReport(id, email);
        return ResponseEntity.ok(Map.of("message", "Report deleted successfully"));
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
