package com.incidenttracker.service;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.exception.InvalidTransitionException;
import com.incidenttracker.model.Incident;
import com.incidenttracker.model.Status;
import com.incidenttracker.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentService {
    private final IncidentRepository repository;
    private final Sinks.Many<IncidentResponse> sink = Sinks.many().multicast().onBackpressureBuffer();

    public Mono<IncidentResponse> createIncident(IncidentRequest request) {
        Incident incident = new Incident();
        incident.setId(UUID.randomUUID().toString());
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setStatus(Status.OPEN);
        incident.setAssignedTo(request.getAssignedTo());
        incident.setCreatedAt(Instant.now());
        incident.setUpdatedAt(Instant.now());

        return repository.save(incident)
                .map(this::toResponse)
                .doOnNext(response -> sink.tryEmitNext(response));
    }

    public Flux<IncidentResponse> getAllIncidents() {
        return repository.findAll().map(this::toResponse);
    }

    public Mono<IncidentResponse> getIncidentById(String id) {
        return repository.findById(id)
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Incident not found")));
    }

    public Mono<IncidentResponse> acknowledgeIncident(String id) {
        return repository.findById(id)
                .flatMap(incident -> {
                    if (incident.getStatus() != Status.OPEN) {
                        return Mono.error(new InvalidTransitionException(
                                "Cannot acknowledge incident. Current status: " + incident.getStatus()));
                    }
                    incident.setStatus(Status.ACKNOWLEDGED);
                    incident.setUpdatedAt(Instant.now());
                    return repository.save(incident);
                })
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Incident not found")));
    }

    public Mono<IncidentResponse> resolveIncident(String id) {
        return repository.findById(id)
                .flatMap(incident -> {
                    if (incident.getStatus() == Status.RESOLVED) {
                        return Mono.error(new InvalidTransitionException(
                                "Incident is already resolved"));
                    }
                    if (incident.getStatus() == Status.OPEN) {
                        return Mono.error(new InvalidTransitionException(
                                "Cannot resolve incident from OPEN status. Must be ACKNOWLEDGED first"));
                    }
                    incident.setStatus(Status.RESOLVED);
                    incident.setUpdatedAt(Instant.now());
                    return repository.save(incident);
                })
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Incident not found")));
    }

    public Mono<Void> deleteIncident(String id) {
        return repository.findById(id)
                .flatMap(incident -> {
                    if (incident.getStatus() != Status.RESOLVED) {
                        return Mono.error(new InvalidTransitionException(
                                "Cannot delete incident. Only RESOLVED incidents can be deleted"));
                    }
                    return repository.deleteById(id);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Incident not found")));
    }

    public Flux<IncidentResponse> streamIncidents() {
        return sink.asFlux();
    }

    private IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getAssignedTo(),
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }
}
