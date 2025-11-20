package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.model.ReportStatus;
import com.example.disastermanagement.service.DisasterReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final DisasterReportService reportService;

    public AdminReportController(DisasterReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/all")
    public List<DisasterReport> all() {
        return reportService.getAll();
    }

    @GetMapping("/pending")
    public List<DisasterReport> pending() {
        return reportService.getByStatus(ReportStatus.PENDING);
    }

    @GetMapping("/approved")
    public List<DisasterReport> approved() {
        return reportService.getByStatus(ReportStatus.APPROVED);
    }

    @GetMapping("/resolved")
    public List<DisasterReport> resolved() {
        return reportService.getByStatus(ReportStatus.RESOLVED);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        DisasterReport report = reportService.updateStatus(id, ReportStatus.APPROVED);
        return ResponseEntity.ok(Map.of(
                "message", "Report approved",
                "id", report.getId()
        ));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        reportService.updateStatus(id, ReportStatus.REJECTED);
        return ResponseEntity.ok(Map.of(
                "message", "Report rejected",
                "id", id
        ));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable Long id) {
        reportService.updateStatus(id, ReportStatus.RESOLVED);
        return ResponseEntity.ok(Map.of(
                "message", "Report marked as resolved",
                "id", id
        ));
    }
}


