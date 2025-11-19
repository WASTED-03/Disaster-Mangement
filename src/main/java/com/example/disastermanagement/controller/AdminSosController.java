package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.SosRequest;
import com.example.disastermanagement.service.SosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/sos")
public class AdminSosController {

    @Autowired
    private SosService sosService;

    // Get all SOS alerts
    @GetMapping("/all")
    public List<SosRequest> getAllSos() {
        return sosService.getAll();
    }

    // Filter by user email
    @GetMapping("/by-email")
    public List<SosRequest> getByEmail(@RequestParam String email) {
        return sosService.getByEmail(email);
    }

    // Filter by date: format YYYY-MM-DD
    @GetMapping("/by-date")
    public List<SosRequest> getByDate(@RequestParam String date) {
        LocalDate d = LocalDate.parse(date);
        LocalDateTime start = d.atStartOfDay();
        LocalDateTime end = d.atTime(23,59,59);

        return sosService.getByDate(start, end);
    }
}
