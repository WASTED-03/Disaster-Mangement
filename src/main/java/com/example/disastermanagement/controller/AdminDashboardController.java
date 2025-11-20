package com.example.disastermanagement.controller;

import com.example.disastermanagement.repository.SosRepository.TypeCount;
import com.example.disastermanagement.service.SosService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final SosService sosService;

    public AdminDashboardController(SosService sosService) {
        this.sosService = sosService;
    }

    @GetMapping("/status")
    public SosService.StatusSummary statusSummary() {
        return sosService.statusSummary();
    }

    @GetMapping("/daily")
    public SosService.DailySummary dailySummary() {
        return sosService.dailySummary();
    }

    @GetMapping("/types")
    public List<TypeCount> typeSummary() {
        return sosService.typeSummary();
    }
}


