# API Contract - Incident Tracker

## Base URL
```
http://localhost:8080
```

## Endpoints

### 1. Create Incident
**POST** `/incidents`

**Request Body:**
```json
{
  "title": "string (required)",
  "description": "string (optional)",
  "severity": "P1 | P2 | P3 (required)",
  "assignedTo": "string (optional)"
}
```

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "title": "string",
  "description": "string",
  "severity": "P1 | P2 | P3",
  "status": "OPEN",
  "assignedTo": "string",
  "createdAt": "2026-04-27T12:00:00Z",
  "updatedAt": "2026-04-27T12:00:00Z"
}
```

### 2. Get All Incidents
**GET** `/incidents`

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "title": "string",
    "description": "string",
    "severity": "P1 | P2 | P3",
    "status": "OPEN | ACKNOWLEDGED | RESOLVED",
    "assignedTo": "string",
    "createdAt": "2026-04-27T12:00:00Z",
    "updatedAt": "2026-04-27T12:00:00Z"
  }
]
```

### 3. Get Incident by ID
**GET** `/incidents/{id}`

**Response:** `200 OK` (same as single incident above)

### 4. Acknowledge Incident
**PATCH** `/incidents/{id}/acknowledge`

**Response:** `200 OK` (incident with status = ACKNOWLEDGED)

### 5. Resolve Incident
**PATCH** `/incidents/{id}/resolve`

**Response:** `200 OK` (incident with status = RESOLVED)

### 6. Delete Incident
**DELETE** `/incidents/{id}`

**Response:** `204 No Content`

### 7. Stream Incidents (SSE)
**GET** `/incidents/stream`

**Headers:** `Accept: text/event-stream`

**Response:** Stream of incidents as they are created

## Enums

### Severity
- `P1` - Critical priority
- `P2` - High priority
- `P3` - Medium priority

### Status
- `OPEN` - Initial state
- `ACKNOWLEDGED` - Incident has been acknowledged
- `RESOLVED` - Incident has been resolved

## Status Transitions

```
OPEN → ACKNOWLEDGED → RESOLVED
```

**Rules:**
- Cannot go back to OPEN once ACKNOWLEDGED
- Cannot modify if already RESOLVED
- Can only delete if RESOLVED

## Error Responses

### 400 Bad Request (Validation Error)
```json
{
  "error": "title: Title is required",
  "status": 400
}
```

### 404 Not Found
```json
{
  "error": "Incident not found",
  "status": 404
}
```

### 422 Unprocessable Entity (Invalid Transition)
```json
{
  "error": "Cannot acknowledge incident. Current status: RESOLVED",
  "status": 422
}
```

## Date Format
All dates use ISO 8601 format: `2026-04-27T12:00:00Z`

## CORS
All origins are allowed for development purposes.
