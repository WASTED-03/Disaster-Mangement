package com.example.disastermanagement.controller;

import com.example.disastermanagement.config.JwtUtil;
import com.example.disastermanagement.model.SosRequest;
import com.example.disastermanagement.service.SosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sos")
public class SosController {

    @Autowired
    private SosService sosService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/send")
    public String sendSos(@RequestBody SosRequest sosRequest) {
        sosService.createSos(sosRequest);
        return "SOS Alert Sent Successfully!";
    }
    @GetMapping("/my")
public List<SosRequest> getMySos(@RequestHeader("Authorization") String token) {
    String email = jwtUtil.extractUsername(token.substring(7));
    return sosService.getByEmail(email);
}

}
