package com.incidenttracker.dto;

import com.incidenttracker.model.Severity;
import com.incidenttracker.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResponse {
    private String id;
    private String title;
    private String description;
    private Severity severity;
    private Status status;
    private String assignedTo;
    private Instant createdAt;
    private Instant updatedAt;
}
