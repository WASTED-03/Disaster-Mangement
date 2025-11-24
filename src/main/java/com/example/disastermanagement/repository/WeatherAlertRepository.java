package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.WeatherAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for WeatherAlert entity.
 */
public interface WeatherAlertRepository extends JpaRepository<WeatherAlert, Long> {
    
    /**
     * Find all alerts after a specific timestamp.
     * Used for fetching recent alerts.
     * 
     * @param time The timestamp to search after
     * @return List of alerts after the given time, ordered by timestamp descending
     */
    List<WeatherAlert> findByTimestampAfterOrderByTimestampDesc(LocalDateTime time);
    
    /**
     * Find all alerts, ordered by timestamp descending.
     * 
     * @return List of all alerts, ordered by timestamp descending
     */
    List<WeatherAlert> findAllByOrderByTimestampDesc();
}

