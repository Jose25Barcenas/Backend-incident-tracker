package com.incidenttracker.controller;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.exception.InvalidTransitionException;
import com.incidenttracker.service.IncidentService;
import com.incidenttracker.service.IncidentStreamService;
import com.incidenttracker.util.UuidValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {
    private final IncidentService service;
    private final IncidentStreamService streamService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IncidentResponse> createIncident(@Valid @RequestBody IncidentRequest request) {
        log.info("Creating new incident with title: {}", request.getTitle());
        return service.createIncident(request)
                .doOnSuccess(incident -> log.info("Incident created with id: {}", incident.getId()))
                .doOnError(error -> log.error("Error creating incident", error));
    }

    @GetMapping
    public Flux<IncidentResponse> getAllIncidents() {
        log.debug("Fetching all incidents");
        return service.getAllIncidents();
    }

    @GetMapping("/{id}")
    public Mono<IncidentResponse> getIncidentById(@PathVariable String id) {
        if (!UuidValidator.isValidUuid(id)) {
            log.warn("Invalid UUID format: {}", id);
            return Mono.error(new InvalidTransitionException("Invalid incident ID format"));
        }
        log.debug("Fetching incident with id: {}", id);
        return service.getIncidentById(id);
    }

    @PatchMapping("/{id}/acknowledge")
    public Mono<IncidentResponse> acknowledgeIncident(@PathVariable String id) {
        if (!UuidValidator.isValidUuid(id)) {
            log.warn("Invalid UUID format: {}", id);
            return Mono.error(new InvalidTransitionException("Invalid incident ID format"));
        }
        log.info("Acknowledging incident with id: {}", id);
        return service.acknowledgeIncident(id)
                .doOnSuccess(incident -> log.info("Incident acknowledged: {}", id))
                .doOnError(error -> log.error("Error acknowledging incident: {}", id, error));
    }

    @PatchMapping("/{id}/resolve")
    public Mono<IncidentResponse> resolveIncident(@PathVariable String id) {
        if (!UuidValidator.isValidUuid(id)) {
            log.warn("Invalid UUID format: {}", id);
            return Mono.error(new InvalidTransitionException("Invalid incident ID format"));
        }
        log.info("Resolving incident with id: {}", id);
        return service.resolveIncident(id)
                .doOnSuccess(incident -> log.info("Incident resolved: {}", id))
                .doOnError(error -> log.error("Error resolving incident: {}", id, error));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteIncident(@PathVariable String id) {
        if (!UuidValidator.isValidUuid(id)) {
            log.warn("Invalid UUID format: {}", id);
            return Mono.error(new InvalidTransitionException("Invalid incident ID format"));
        }
        log.info("Deleting incident with id: {}", id);
        return service.deleteIncident(id)
                .doOnSuccess(v -> log.info("Incident deleted: {}", id))
                .doOnError(error -> log.error("Error deleting incident: {}", id, error));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<IncidentResponse> streamIncidents() {
        log.debug("Client connected to incident stream");
        return streamService.getStream()
                .doOnCancel(() -> log.debug("Client disconnected from incident stream"));
    }
}
