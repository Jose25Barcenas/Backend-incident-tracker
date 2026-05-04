package com.incidenttracker.controller;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.model.Severity;
import com.incidenttracker.model.Status;
import com.incidenttracker.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(IncidentController.class)
class IncidentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IncidentService service;

    @Test
    void createIncident_shouldReturn201() {
        IncidentRequest request = new IncidentRequest();
        request.setTitle("Test Incident");
        request.setSeverity(Severity.P1);

        IncidentResponse response = new IncidentResponse(
                "test-id", "Test Incident", null, Severity.P1,
                Status.OPEN, null, Instant.now(), Instant.now()
        );

        when(service.createIncident(any(IncidentRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-id")
                .jsonPath("$.title").isEqualTo("Test Incident")
                .jsonPath("$.status").isEqualTo("OPEN");
    }

    @Test
    void getAllIncidents_shouldReturn200() {
        IncidentResponse response = new IncidentResponse(
                "test-id", "Test Incident", null, Severity.P1,
                Status.OPEN, null, Instant.now(), Instant.now()
        );

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
        IncidentResponse response = new IncidentResponse(
                "test-id", "Test Incident", null, Severity.P1,
                Status.OPEN, null, Instant.now(), Instant.now()
        );

        when(service.getIncidentById(anyString())).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/incidents/test-id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-id");
    }

    @Test
    void acknowledgeIncident_shouldReturn200() {
        IncidentResponse response = new IncidentResponse(
                "test-id", "Test Incident", null, Severity.P1,
                Status.ACKNOWLEDGED, null, Instant.now(), Instant.now()
        );

        when(service.acknowledgeIncident(anyString())).thenReturn(Mono.just(response));

        webTestClient.patch()
                .uri("/incidents/test-id/acknowledge")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("ACKNOWLEDGED");
    }

    @Test
    void resolveIncident_shouldReturn200() {
        IncidentResponse response = new IncidentResponse(
                "test-id", "Test Incident", null, Severity.P1,
                Status.RESOLVED, null, Instant.now(), Instant.now()
        );

        when(service.resolveIncident(anyString())).thenReturn(Mono.just(response));

        webTestClient.patch()
                .uri("/incidents/test-id/resolve")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("RESOLVED");
    }

    @Test
    void deleteIncident_shouldReturn204() {
        when(service.deleteIncident(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/incidents/test-id")
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
