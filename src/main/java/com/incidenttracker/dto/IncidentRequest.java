package com.incidenttracker.dto;

import com.incidenttracker.model.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidentRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Severity is required")
    private Severity severity;
    
    private String assignedTo;
}
