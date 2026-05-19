package com.incidenttracker.service;

import com.incidenttracker.dto.IncidentRequest;
import com.incidenttracker.dto.IncidentResponse;
import com.incidenttracker.exception.IncidentNotFoundException;
import com.incidenttracker.mapper.IncidentMapper;
import com.incidenttracker.model.Incident;
import com.incidenttracker.model.Status;
import com.incidenttracker.repository.IncidentRepository;
import com.incidenttracker.validator.StatusTransitionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentService {
    private final IncidentRepository repository;
    private final IncidentMapper mapper;
    private final StatusTransitionValidator validator;
    private final IncidentStreamService streamService;

    public Mono<IncidentResponse> createIncident(IncidentRequest request) {
        return Mono.just(request)
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toResponse)
                .doOnNext(streamService::emitIncident)
                .doOnError(error -> log.error("Error creating incident", error))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<IncidentResponse> getAllIncidents() {
        return repository.findAll()
                .parallel()
                .runOn(Schedulers.parallel())
                .map(mapper::toResponse)
                .sequential()
                .doOnError(error -> log.error("Error fetching incidents", error));
    }

    public Mono<IncidentResponse> getIncidentById(String id) {
        return findIncidentOrThrow(id)
                .map(mapper::toResponse);
    }

    public Mono<IncidentResponse> acknowledgeIncident(String id) {
        return findIncidentOrThrow(id)
                .flatMap(incident -> updateStatus(incident, Status.ACKNOWLEDGED))
                .doOnError(error -> log.error("Error acknowledging incident {}", id, error))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<IncidentResponse> resolveIncident(String id) {
        return findIncidentOrThrow(id)
                .flatMap(incident -> updateStatus(incident, Status.RESOLVED))
                .doOnError(error -> log.error("Error resolving incident {}", id, error))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteIncident(String id) {
        return findIncidentOrThrow(id)
                .doOnNext(incident -> validator.validateDeletion(incident.getStatus()))
                .flatMap(incident -> repository.deleteById(id))
                .doOnError(error -> log.error("Error deleting incident {}", id, error))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<IncidentResponse> updateStatus(Incident incident, Status newStatus) {
        validator.validateTransition(incident.getStatus(), newStatus);
        
        Incident updatedIncident = Incident.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .severity(incident.getSeverity())
                .status(newStatus)
                .assignedTo(incident.getAssignedTo())
                .createdAt(incident.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
        
        return repository.save(updatedIncident)
                .map(mapper::toResponse);
    }

    private Mono<Incident> findIncidentOrThrow(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IncidentNotFoundException(id)));
    }
}
