package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.DisasterReport;
import com.example.disastermanagement.service.DisasterReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class DisasterReportController {

	private final DisasterReportService reportService;

	public DisasterReportController(DisasterReportService reportService) {
		this.reportService = reportService;
	}

	@PostMapping
	public ResponseEntity<DisasterReport> create(@RequestBody DisasterReport report) {
		return ResponseEntity.ok(reportService.createReport(report));
	}

	@GetMapping("/me")
	public ResponseEntity<List<DisasterReport>> myReports(@RequestParam String email) {
		return ResponseEntity.ok(reportService.getUserReports(email));
	}

	@GetMapping
	public ResponseEntity<List<DisasterReport>> all() {
		return ResponseEntity.ok(reportService.getAllReports());
	}
}


