package com.example.disastermanagement.service;

import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.model.ReportStatus;
import com.example.disastermanagement.repository.DisasterReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class DisasterReportService {

    private static final Set<String> ALLOWED_SEVERITIES = Set.of("LOW", "MEDIUM", "HIGH");

    private final DisasterReportRepository repo;

    public DisasterReportService(DisasterReportRepository repo) {
        this.repo = repo;
    }

    public DisasterReport createReport(DisasterReport report, String email) {
        report.setUserEmail(email);
        report.setTimestamp(LocalDateTime.now());
        report.setStatus(ReportStatus.PENDING);
        if (!StringUtils.hasText(report.getSeverity())) {
            report.setSeverity("MEDIUM");
        }
        report.setSeverity(report.getSeverity().toUpperCase());
        if (!ALLOWED_SEVERITIES.contains(report.getSeverity())) {
            throw new IllegalArgumentException("Severity must be LOW, MEDIUM, or HIGH");
        }
        return repo.save(report);
    }

    public List<DisasterReport> getAll() {
        return repo.findAll();
    }

    public List<DisasterReport> getByUser(String email) {
        return repo.findByUserEmail(email);
    }

    public List<DisasterReport> getByStatus(ReportStatus status) {
        return repo.findByStatus(status);
    }

    public DisasterReport updateStatus(long id, ReportStatus status) {
        DisasterReport report = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(status);
        return repo.save(report);
    }
}
