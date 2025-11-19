package com.example.disastermanagement.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/public")
    public String publicTest() {
        return "Public OK";
    }

    @GetMapping("/private")
    public String privateTest() {
        return "Private OK";
    }
}
