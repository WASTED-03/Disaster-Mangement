package com.example.disastermanagement.dto;

import com.example.disastermanagement.model.enums.AlertSeverity;
import com.example.disastermanagement.model.enums.AlertType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertFilterCriteria {
    private AlertType alertType;
    private AlertSeverity severity;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean acknowledged;
}
