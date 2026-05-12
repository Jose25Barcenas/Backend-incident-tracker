package com.incidenttracker.controller;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.service.IncidentService;
import com.incidenttracker.service.IncidentStreamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {
    private final IncidentService service;
    private final IncidentStreamService streamService;

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
        return streamService.getStream();
    }
}
