package com.example.disastermanagement.service;

import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.repository.DisasterReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DisasterReportService {

    private final DisasterReportRepository repository;

    public DisasterReportService(DisasterReportRepository repository) {
        this.repository = repository;
    }

    public DisasterReport createReport(DisasterReport report) {
        report.setTimestamp(LocalDateTime.now());
        return repository.save(report);
    }

    public List<DisasterReport> getUserReports(String email) {
        return repository.findByUserEmail(email);
    }

    public List<DisasterReport> getAllReports() {
        return repository.findAll();
    }
}
