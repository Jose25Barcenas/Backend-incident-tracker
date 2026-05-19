package com.incidenttracker.validator;

import com.incidenttracker.exception.InvalidTransitionException;
import com.incidenttracker.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTransitionValidatorTest {

    private StatusTransitionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StatusTransitionValidator();
    }

    @Test
    void testValidAcknowledgeTransition() {
        assertDoesNotThrow(() -> validator.validateTransition(Status.OPEN, Status.ACKNOWLEDGED));
    }

    @Test
    void testInvalidAcknowledgeFromAcknowledged() {
        assertThrows(InvalidTransitionException.class, 
                () -> validator.validateTransition(Status.ACKNOWLEDGED, Status.ACKNOWLEDGED));
    }

    @Test
    void testValidResolveTransition() {
        assertDoesNotThrow(() -> validator.validateTransition(Status.ACKNOWLEDGED, Status.RESOLVED));
    }

    @Test
    void testInvalidResolveFromOpen() {
        assertThrows(InvalidTransitionException.class, 
                () -> validator.validateTransition(Status.OPEN, Status.RESOLVED));
    }

    @Test
    void testInvalidResolveFromResolved() {
        assertThrows(InvalidTransitionException.class, 
                () -> validator.validateTransition(Status.RESOLVED, Status.RESOLVED));
    }

    @Test
    void testValidDeletion() {
        assertDoesNotThrow(() -> validator.validateDeletion(Status.RESOLVED));
    }

    @Test
    void testInvalidDeletionFromOpen() {
        assertThrows(InvalidTransitionException.class, 
                () -> validator.validateDeletion(Status.OPEN));
    }

    @Test
    void testInvalidDeletionFromAcknowledged() {
        assertThrows(InvalidTransitionException.class, 
                () -> validator.validateDeletion(Status.ACKNOWLEDGED));
    }
}
