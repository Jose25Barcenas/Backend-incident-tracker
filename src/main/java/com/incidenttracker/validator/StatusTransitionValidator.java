package com.incidenttracker.validator;

import com.incidenttracker.exception.InvalidTransitionException;
import com.incidenttracker.model.Status;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
public class StatusTransitionValidator {

    private final Map<Status, Consumer<Status>> transitionRules = Map.of(
            Status.ACKNOWLEDGED, this::validateAcknowledgeTransition,
            Status.RESOLVED, this::validateResolveTransition
    );

    public void validateTransition(Status currentStatus, Status targetStatus) {
        transitionRules.getOrDefault(targetStatus, status -> {})
                .accept(currentStatus);
    }

    public void validateDeletion(Status currentStatus) {
        if (currentStatus != Status.RESOLVED) {
            throw new InvalidTransitionException(
                    "Cannot delete incident. Only RESOLVED incidents can be deleted");
        }
    }

    private void validateAcknowledgeTransition(Status currentStatus) {
        if (currentStatus != Status.OPEN) {
            throw new InvalidTransitionException(
                    "Cannot acknowledge incident. Current status: " + currentStatus);
        }
    }

    private void validateResolveTransition(Status currentStatus) {
        if (currentStatus == Status.RESOLVED) {
            throw new InvalidTransitionException("Incident is already resolved");
        }
        if (currentStatus == Status.OPEN) {
            throw new InvalidTransitionException(
                    "Cannot resolve incident from OPEN status. Must be ACKNOWLEDGED first");
        }
    }
}
