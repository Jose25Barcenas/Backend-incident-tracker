package com.incidenttracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incidents")
public class Incident {
    @Id
    private String id;
    private String title;
    private String description;
    private Severity severity;
    private Status status;
    private String assignedTo;
    private Instant createdAt;
    private Instant updatedAt;
}
