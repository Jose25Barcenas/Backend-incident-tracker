package com.incidenttracker.repository;

import com.incidenttracker.model.Incident;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class IncidentRepository {
    private final Map<String, Incident> incidents = new ConcurrentHashMap<>();

    public Mono<Incident> save(Incident incident) {
        return Mono.fromCallable(() -> {
            incidents.put(incident.getId(), incident);
            return incident;
        });
    }

    public Flux<Incident> findAll() {
        return Flux.fromIterable(incidents.values());
    }

    public Mono<Incident> findById(String id) {
        return Mono.justOrEmpty(incidents.get(id));
    }

    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> incidents.remove(id));
    }
}
