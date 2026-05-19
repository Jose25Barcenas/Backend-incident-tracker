package com.incidenttracker.util;

import java.util.UUID;

public class UuidValidator {
    
    public static boolean isValidUuid(String uuid) {
        if (uuid == null) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
