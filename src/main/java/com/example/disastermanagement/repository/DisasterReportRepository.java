package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.DisasterReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisasterReportRepository extends JpaRepository<DisasterReport, Long> {
	List<DisasterReport> findByUserEmail(String userEmail);
}


