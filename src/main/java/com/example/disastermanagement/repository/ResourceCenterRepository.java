package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.ResourceCenter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceCenterRepository extends JpaRepository<ResourceCenter, Long> {
    List<ResourceCenter> findByType(String type);
}

