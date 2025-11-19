package com.example.disastermanagement.service;

import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.repository.DisasterReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DisasterReportService {

    private final DisasterReportRepository repo;

    public DisasterReportService(DisasterReportRepository repo) {
        this.repo = repo;
    }

    public DisasterReport createReport(DisasterReport r) {
        r.setTimestamp(LocalDateTime.now());
        return repo.save(r);
    }

    public List<DisasterReport> getAll() {
        return repo.findAll();
    }

    public List<DisasterReport> getByUser(String email) {
        return repo.findByUserEmail(email);
    }
}
