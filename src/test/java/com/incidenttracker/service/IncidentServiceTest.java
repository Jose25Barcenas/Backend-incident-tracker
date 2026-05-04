package com.incidenttracker.service;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.exception.IncidentNotFoundException;
import com.incidenttracker.exception.InvalidTransitionException;
import com.incidenttracker.model.Incident;
import com.incidenttracker.model.Severity;
import com.incidenttracker.model.Status;
import com.incidenttracker.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository repository;

    @InjectMocks
    private IncidentService service;

    private Incident testIncident;

    @BeforeEach
    void setUp() {
        testIncident = new Incident();
        testIncident.setId("test-id");
        testIncident.setTitle("Test Incident");
        testIncident.setDescription("Test Description");
        testIncident.setSeverity(Severity.P1);
        testIncident.setStatus(Status.OPEN);
        testIncident.setAssignedTo("test-user");
        testIncident.setCreatedAt(Instant.now());
        testIncident.setUpdatedAt(Instant.now());
    }

    @Test
    void createIncident_shouldCreateSuccessfully() {
        IncidentRequest request = new IncidentRequest();
        request.setTitle("New Incident");
        request.setDescription("Description");
        request.setSeverity(Severity.P1);
        request.setAssignedTo("user");

        when(repository.save(any(Incident.class))).thenReturn(Mono.just(testIncident));

        StepVerifier.create(service.createIncident(request))
                .assertNext(response -> {
                    assertThat(response.getTitle()).isEqualTo("Test Incident");
                    assertThat(response.getStatus()).isEqualTo(Status.OPEN);
                    assertThat(response.getSeverity()).isEqualTo(Severity.P1);
                })
                .verifyComplete();
    }

    @Test
    void getAllIncidents_shouldReturnAllIncidents() {
        when(repository.findAll()).thenReturn(Flux.just(testIncident));

        StepVerifier.create(service.getAllIncidents())
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo("test-id");
                    assertThat(response.getTitle()).isEqualTo("Test Incident");
                })
                .verifyComplete();
    }

    @Test
    void getIncidentById_shouldReturnIncident() {
        when(repository.findById("test-id")).thenReturn(Mono.just(testIncident));

        StepVerifier.create(service.getIncidentById("test-id"))
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo("test-id");
                    assertThat(response.getTitle()).isEqualTo("Test Incident");
                })
                .verifyComplete();
    }

    @Test
    void getIncidentById_shouldThrowNotFoundException() {
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(service.getIncidentById("non-existent"))
                .expectError(IncidentNotFoundException.class)
                .verify();
    }

    @Test
    void acknowledgeIncident_shouldChangeStatusToAcknowledged() {
        testIncident.setStatus(Status.OPEN);
        when(repository.findById("test-id")).thenReturn(Mono.just(testIncident));
        when(repository.save(any(Incident.class))).thenReturn(Mono.just(testIncident));

        StepVerifier.create(service.acknowledgeIncident("test-id"))
                .assertNext(response -> {
                    assertThat(response.getStatus()).isEqualTo(Status.ACKNOWLEDGED);
                })
                .verifyComplete();
    }

    @Test
    void acknowledgeIncident_shouldFailIfNotOpen() {
        testIncident.setStatus(Status.RESOLVED);
        when(repository.findById("test-id")).thenReturn(Mono.just(testIncident));

        StepVerifier.create(service.acknowledgeIncident("test-id"))
                .expectError(InvalidTransitionException.class)
                .verify();
    }

    @Test
    void resolveIncident_shouldChangeStatusToResolved() {
        testIncident.setStatus(Status.ACKNOWLEDGED);
        when(repository.findById("test-id")).thenReturn(Mono.just(testIncident));
        when(repository.save(any(Incident.class))).thenReturn(Mono.just(testIncident));

        StepVerifier.create(service.resolveIncident("test-id"))
                .assertNext(response -> {
                    assertThat(response.getStatus()).isEqualTo(Status.RESOLVED);
                })
                .verifyComplete();
    }

    @Test
    void resolveIncident_shouldFailIfOpen() {
        testIncident.setStatus(Status.OPEN);
        when(repository.findById("test-id")).thenReturn(Mono.just(testIncident));

        StepVerifier.create(service.resolveIncident("test-id"))
                .expectError(InvalidTransitionException.class)
                .verify();
    }

    @Test
    void resolveIncident_shouldFailIfAlreadyResolved() {
        testIncident.setStatus(Status.RESOLVED);
        when(repository.findById("test-id")).thenReturn(Mono.just(testIncident));

        StepVerifier.create(service.resolveIncident("test-id"))
                .expectError(InvalidTransitionException.class)
                .verify();
    }

    @Test
    void deleteIncident_shouldDeleteIfResolved() {
        testIncident.setStatus(Status.RESOLVED);
        when(repository.findById("test-id")).thenReturn(Mono.just(testIncident));
        when(repository.deleteById("test-id")).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteIncident("test-id"))
                .verifyComplete();
    }

    @Test
    void deleteIncident_shouldFailIfNotResolved() {
        testIncident.setStatus(Status.OPEN);
        when(repository.findById("test-id")).thenReturn(Mono.just(testIncident));

        StepVerifier.create(service.deleteIncident("test-id"))
                .expectError(InvalidTransitionException.class)
                .verify();
    }
}
