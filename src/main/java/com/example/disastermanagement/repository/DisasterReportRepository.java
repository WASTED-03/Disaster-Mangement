package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisasterReportRepository extends JpaRepository<DisasterReport, Long> {
    List<DisasterReport> findByUserEmail(String email);
    List<DisasterReport> findByStatus(ReportStatus status);
    List<DisasterReport> findByUserEmailAndStatus(String email, ReportStatus status);
    long countByUserEmail(String email);
    long countByUserEmailAndStatus(String email, ReportStatus status);
}
