package com.example.disastermanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "Access granted! You are authenticated.";
    }
}
