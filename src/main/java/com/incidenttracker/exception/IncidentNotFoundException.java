package com.incidenttracker.exception;

public class IncidentNotFoundException extends RuntimeException {
    public IncidentNotFoundException(String id) {
        super("Incident not found with id: " + id);
    }
}
