package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.ResourceCenter;
import com.example.disastermanagement.service.ResourceCenterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceCenterController {

    private final ResourceCenterService resourceService;

    public ResourceCenterController(ResourceCenterService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResourceCenter>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/near")
    public ResponseEntity<List<ResourceCenter>> getResourcesNear(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(resourceService.getResourcesNear(lat, lng));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ResourceCenter>> getResourcesByType(@PathVariable String type) {
        return ResponseEntity.ok(resourceService.getResourcesByType(type));
    }
}

