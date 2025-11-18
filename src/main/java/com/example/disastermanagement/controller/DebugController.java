package com.example.disastermanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

	@GetMapping("/public")
	public ResponseEntity<String> publicEndpoint() {
		return ResponseEntity.ok("Public OK");
	}

	@GetMapping("/private")
	public ResponseEntity<String> privateEndpoint() {
		return ResponseEntity.ok("Private OK");
	}
}


