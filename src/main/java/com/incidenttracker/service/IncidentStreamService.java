package com.incidenttracker.service;

import com.incidenttracker.dto.IncidentResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class IncidentStreamService {
    
    private final Sinks.Many<IncidentResponse> sink = 
            Sinks.many().multicast().onBackpressureBuffer();

    public void emitIncident(IncidentResponse incident) {
        sink.tryEmitNext(incident);
    }

    public Flux<IncidentResponse> getStream() {
        return sink.asFlux();
    }
}
