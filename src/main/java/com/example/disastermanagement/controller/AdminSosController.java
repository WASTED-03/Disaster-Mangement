package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.SosRequest;
import com.example.disastermanagement.service.SosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sos")
public class AdminSosController {

    @Autowired
    private SosService sosService;

    // Fetch all SOS alerts
    @GetMapping("/all")
    public List<SosRequest> getAllSos() {
        return sosService.getAllSos();
    }

    // Filter by email
    @GetMapping("/by-email")
    public List<SosRequest> getByEmail(@RequestParam String email) {
        return sosService.getByEmail(email);
    }

    // Filter by date
    @GetMapping("/by-date")
    public List<SosRequest> getByDate(@RequestParam String date) {
        return sosService.getByDate(date);
    }
}
