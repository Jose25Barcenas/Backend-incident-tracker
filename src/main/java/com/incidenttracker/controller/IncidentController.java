package com.incidenttracker.controller;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {
    private final IncidentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IncidentResponse> createIncident(@Valid @RequestBody IncidentRequest request) {
        return service.createIncident(request);
    }

    @GetMapping
    public Flux<IncidentResponse> getAllIncidents() {
        return service.getAllIncidents();
    }

    @GetMapping("/{id}")
    public Mono<IncidentResponse> getIncidentById(@PathVariable String id) {
        return service.getIncidentById(id);
    }

    @PatchMapping("/{id}/acknowledge")
    public Mono<IncidentResponse> acknowledgeIncident(@PathVariable String id) {
        return service.acknowledgeIncident(id);
    }

    @PatchMapping("/{id}/resolve")
    public Mono<IncidentResponse> resolveIncident(@PathVariable String id) {
        return service.resolveIncident(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteIncident(@PathVariable String id) {
        return service.deleteIncident(id);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<IncidentResponse> streamIncidents() {
        return service.streamIncidents();
    }
}
