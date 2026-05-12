package com.incidenttracker.mapper;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.model.Incident;
import com.incidenttracker.model.Status;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class IncidentMapper {

    public Incident toEntity(IncidentRequest request) {
        return Incident.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .description(request.getDescription())
                .severity(request.getSeverity())
                .status(Status.OPEN)
                .assignedTo(request.getAssignedTo())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public IncidentResponse toResponse(Incident incident) {
        return IncidentResponse.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .severity(incident.getSeverity())
                .status(incident.getStatus())
                .assignedTo(incident.getAssignedTo())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }
}
