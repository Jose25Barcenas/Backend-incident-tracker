package com.incidenttracker.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UuidValidatorTest {

    @Test
    void testValidUuid() {
        String validUuid = "550e8400-e29b-41d4-a716-446655440000";
        assertTrue(UuidValidator.isValidUuid(validUuid));
    }

    @Test
    void testInvalidUuid() {
        String invalidUuid = "not-a-uuid";
        assertFalse(UuidValidator.isValidUuid(invalidUuid));
    }

    @Test
    void testEmptyString() {
        assertFalse(UuidValidator.isValidUuid(""));
    }

    @Test
    void testNullString() {
        assertFalse(UuidValidator.isValidUuid(null));
    }
}
