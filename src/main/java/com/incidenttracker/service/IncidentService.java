package com.incidenttracker.service;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.exception.IncidentNotFoundException;
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
        return findIncidentOrThrow(id)
                .map(this::toResponse);
    }

    public Mono<IncidentResponse> acknowledgeIncident(String id) {
        return findIncidentOrThrow(id)
                .flatMap(incident -> {
                    validateTransition(incident.getStatus(), Status.ACKNOWLEDGED);
                    incident.setStatus(Status.ACKNOWLEDGED);
                    incident.setUpdatedAt(Instant.now());
                    return repository.save(incident);
                })
                .map(this::toResponse);
    }

    public Mono<IncidentResponse> resolveIncident(String id) {
        return findIncidentOrThrow(id)
                .flatMap(incident -> {
                    validateTransition(incident.getStatus(), Status.RESOLVED);
                    incident.setStatus(Status.RESOLVED);
                    incident.setUpdatedAt(Instant.now());
                    return repository.save(incident);
                })
                .map(this::toResponse);
    }

    public Mono<Void> deleteIncident(String id) {
        return findIncidentOrThrow(id)
                .flatMap(incident -> {
                    if (incident.getStatus() != Status.RESOLVED) {
                        return Mono.error(new InvalidTransitionException(
                                "Cannot delete incident. Only RESOLVED incidents can be deleted"));
                    }
                    return repository.deleteById(id);
                });
    }

    public Flux<IncidentResponse> streamIncidents() {
        return sink.asFlux();
    }

    private Mono<Incident> findIncidentOrThrow(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IncidentNotFoundException(id)));
    }

    private void validateTransition(Status currentStatus, Status targetStatus) {
        if (targetStatus == Status.ACKNOWLEDGED && currentStatus != Status.OPEN) {
            throw new InvalidTransitionException(
                    "Cannot acknowledge incident. Current status: " + currentStatus);
        }
        if (targetStatus == Status.RESOLVED) {
            if (currentStatus == Status.RESOLVED) {
                throw new InvalidTransitionException("Incident is already resolved");
            }
            if (currentStatus == Status.OPEN) {
                throw new InvalidTransitionException(
                        "Cannot resolve incident from OPEN status. Must be ACKNOWLEDGED first");
            }
        }
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
