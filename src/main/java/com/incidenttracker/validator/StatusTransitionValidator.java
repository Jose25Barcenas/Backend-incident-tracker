package com.incidenttracker.validator;

import com.incidenttracker.exception.InvalidTransitionException;
import com.incidenttracker.model.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusTransitionValidator {

    public void validateTransition(Status currentStatus, Status targetStatus) {
        if (targetStatus == Status.ACKNOWLEDGED) {
            validateAcknowledgeTransition(currentStatus);
        } else if (targetStatus == Status.RESOLVED) {
            validateResolveTransition(currentStatus);
        }
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
