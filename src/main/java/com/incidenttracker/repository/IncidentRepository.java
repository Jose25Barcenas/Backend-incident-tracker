package com.incidenttracker.repository;

import com.incidenttracker.model.Incident;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends ReactiveMongoRepository<Incident, String> {
}
