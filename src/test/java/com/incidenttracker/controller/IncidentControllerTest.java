package com.incidenttracker.controller;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.model.Severity;
import com.incidenttracker.model.Status;
import com.incidenttracker.service.IncidentService;
import com.incidenttracker.service.IncidentStreamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(IncidentController.class)
class IncidentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IncidentService service;

    @MockBean
    private IncidentStreamService streamService;

    private final String validId = UUID.randomUUID().toString();

    @Test
    void createIncident_shouldReturn201() {
        IncidentRequest request = new IncidentRequest();
        request.setTitle("Test Incident");
        request.setSeverity(Severity.P1);

        IncidentResponse response = IncidentResponse.builder()
                .id(validId)
                .title("Test Incident")
                .severity(Severity.P1)
                .status(Status.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(service.createIncident(any(IncidentRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(validId)
                .jsonPath("$.title").isEqualTo("Test Incident")
                .jsonPath("$.status").isEqualTo("OPEN");
    }

    @Test
    void getAllIncidents_shouldReturn200() {
        IncidentResponse response = IncidentResponse.builder()
                .id(validId)
                .title("Test Incident")
                .severity(Severity.P1)
                .status(Status.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(service.getAllIncidents()).thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/incidents")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(IncidentResponse.class)
                .hasSize(1);
    }

    @Test
    void getIncidentById_shouldReturn200() {
        IncidentResponse response = IncidentResponse.builder()
                .id(validId)
                .title("Test Incident")
                .severity(Severity.P1)
                .status(Status.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(service.getIncidentById(anyString())).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/incidents/" + validId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(validId);
    }

    @Test
    void acknowledgeIncident_shouldReturn200() {
        IncidentResponse response = IncidentResponse.builder()
                .id(validId)
                .title("Test Incident")
                .severity(Severity.P1)
                .status(Status.ACKNOWLEDGED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(service.acknowledgeIncident(anyString())).thenReturn(Mono.just(response));

        webTestClient.patch()
                .uri("/incidents/" + validId + "/acknowledge")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("ACKNOWLEDGED");
    }

    @Test
    void resolveIncident_shouldReturn200() {
        IncidentResponse response = IncidentResponse.builder()
                .id(validId)
                .title("Test Incident")
                .severity(Severity.P1)
                .status(Status.RESOLVED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(service.resolveIncident(anyString())).thenReturn(Mono.just(response));

        webTestClient.patch()
                .uri("/incidents/" + validId + "/resolve")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("RESOLVED");
    }

    @Test
    void deleteIncident_shouldReturn204() {
        when(service.deleteIncident(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/incidents/" + validId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void createIncident_shouldReturn400WhenTitleMissing() {
        IncidentRequest request = new IncidentRequest();
        request.setSeverity(Severity.P1);

        webTestClient.post()
                .uri("/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
