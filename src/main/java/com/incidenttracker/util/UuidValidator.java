package com.incidenttracker.util;

import java.util.UUID;

public class UuidValidator {
    
    public static boolean isValidUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
