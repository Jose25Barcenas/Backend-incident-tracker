package com.incidenttracker.mapper;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.model.Incident;
import com.incidenttracker.model.Severity;
import com.incidenttracker.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class IncidentMapperTest {

    private IncidentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IncidentMapper();
    }

    @Test
    void testToEntity() {
        IncidentRequest request = new IncidentRequest();
        request.setTitle("Test Incident");
        request.setDescription("Test Description");
        request.setSeverity(Severity.P1);
        request.setAssignedTo("user@example.com");

        Incident incident = mapper.toEntity(request);

        assertNotNull(incident.getId());
        assertEquals("Test Incident", incident.getTitle());
        assertEquals("Test Description", incident.getDescription());
        assertEquals(Severity.P1, incident.getSeverity());
        assertEquals(Status.OPEN, incident.getStatus());
        assertEquals("user@example.com", incident.getAssignedTo());
        assertNotNull(incident.getCreatedAt());
        assertNotNull(incident.getUpdatedAt());
    }

    @Test
    void testToResponse() {
        Instant now = Instant.now();
        Incident incident = Incident.builder()
                .id("test-id")
                .title("Test Incident")
                .description("Test Description")
                .severity(Severity.P2)
                .status(Status.ACKNOWLEDGED)
                .assignedTo("user@example.com")
                .createdAt(now)
                .updatedAt(now)
                .build();

        IncidentResponse response = mapper.toResponse(incident);

        assertEquals("test-id", response.getId());
        assertEquals("Test Incident", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(Severity.P2, response.getSeverity());
        assertEquals(Status.ACKNOWLEDGED, response.getStatus());
        assertEquals("user@example.com", response.getAssignedTo());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }
}
