package com.example.disastermanagement.service;

import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.model.ReportStatus;
import com.example.disastermanagement.repository.DisasterReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    public List<DisasterReport> getMyReports(String email, ReportStatus status) {
        if (status != null) {
            return repo.findByUserEmailAndStatus(email, status);
        }
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

    public DisasterReport editReport(long id, String userEmail, DisasterReport updates) {
        DisasterReport report = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        // Verify ownership
        if (!report.getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("You can only edit your own reports");
        }

        // Only allow editing if status is PENDING
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalStateException("Cannot edit report. Only PENDING reports can be edited");
        }

        // Update allowed fields
        if (StringUtils.hasText(updates.getType())) {
            report.setType(updates.getType());
        }
        if (StringUtils.hasText(updates.getDescription())) {
            report.setDescription(updates.getDescription());
        }
        if (updates.getLatitude() != 0.0) {
            report.setLatitude(updates.getLatitude());
        }
        if (updates.getLongitude() != 0.0) {
            report.setLongitude(updates.getLongitude());
        }
        if (StringUtils.hasText(updates.getSeverity())) {
            String severity = updates.getSeverity().toUpperCase();
            if (!ALLOWED_SEVERITIES.contains(severity)) {
                throw new IllegalArgumentException("Severity must be LOW, MEDIUM, or HIGH");
            }
            report.setSeverity(severity);
        }

        return repo.save(report);
    }

    public void deleteReport(long id, String userEmail) {
        DisasterReport report = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        // Verify ownership
        if (!report.getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("You can only delete your own reports");
        }

        // Only allow deletion if status is PENDING
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalStateException("Cannot delete report. Only PENDING reports can be deleted");
        }

        repo.delete(report);
    }

    public Map<String, Long> getSummary(String email) {
        long total = repo.countByUserEmail(email);
        long pending = repo.countByUserEmailAndStatus(email, ReportStatus.PENDING);
        long approved = repo.countByUserEmailAndStatus(email, ReportStatus.APPROVED);
        long resolved = repo.countByUserEmailAndStatus(email, ReportStatus.RESOLVED);
        long rejected = repo.countByUserEmailAndStatus(email, ReportStatus.REJECTED);

        return Map.of(
                "totalReports", total,
                "pending", pending,
                "approved", approved,
                "resolved", resolved,
                "rejected", rejected
        );
    }
}
