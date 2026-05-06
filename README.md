# Backend Incident Tracker

Reactive REST API for real-time incident management using Spring WebFlux.

## Requirements

- Docker and Docker Compose (recommended)
- Or Java 17+ and Maven 3.6+ (for local development)

## Execution

### Option 1: With Docker (Recommended - Everything in containers)

```bash
# Start MongoDB and Backend together
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop everything
docker-compose down
```

The application will be available at `http://localhost:8080`

### Option 2: Local development (Only MongoDB in Docker)

```bash
# 1. Start only MongoDB
docker-compose up -d mongodb

# 2. Run backend locally
./mvnw spring-boot:run
```

### Stop services

```bash
docker-compose down
```

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/incidents` | List all incidents |
| GET | `/incidents/stream` | SSE stream of new incidents |
| POST | `/incidents` | Create a new incident |
| GET | `/incidents/{id}` | Get incident by ID |
| PATCH | `/incidents/{id}/acknowledge` | Mark as ACKNOWLEDGED |
| PATCH | `/incidents/{id}/resolve` | Mark as RESOLVED |
| DELETE | `/incidents/{id}` | Delete (only if RESOLVED) |

## Data Model

### Incident

```json
{
  "id": "uuid",
  "title": "string",
  "description": "string",
  "severity": "P1 | P2 | P3",
  "status": "OPEN | ACKNOWLEDGED | RESOLVED",
  "assignedTo": "string (nullable)",
  "createdAt": "ISO 8601",
  "updatedAt": "ISO 8601"
}
```

### Lifecycle

```
OPEN → ACKNOWLEDGED → RESOLVED
```

- An incident cannot go back to OPEN once ACKNOWLEDGED
- An incident cannot be modified if already RESOLVED
- Only RESOLVED incidents can be deleted

## Usage Examples

### Option 1: Postman (Recommended)
1. Import the collection: `Incident-Tracker-API.postman_collection.json`
2. Execute requests in order
3. Copy the `id` from the response and paste it into the `incidentId` variable in Postman

### Option 2: HTTP File
See `incidents.http` file for examples of all operations (compatible with IntelliJ/VS Code).

## Tests performed

**All endpoints tested and working correctly:**
- POST /incidents - Create incident (Status 201)
- GET /incidents - List all incidents (Status 200)
- PATCH /incidents/{id}/acknowledge - Change to ACKNOWLEDGED (Status 200)
- PATCH /incidents/{id}/resolve - Change to RESOLVED (Status 200)
- DELETE /incidents/{id} - Delete RESOLVED incident (Status 204)

**Lifecycle validations working:**
- Error 422 when trying to acknowledge a RESOLVED incident
- Error 422 when trying to delete a non-RESOLVED incident
- Error 422 when trying to resolve from OPEN (must be ACKNOWLEDGED first)

## Estructura del proyecto

```
src/main/java/com/incidenttracker/
├── config/          # Configuration (CORS)
├── controller/      # REST controllers
├── dto/             # DTOs (Request/Response)
├── exception/       # Exceptions and handlers
├── model/           # Domain entities
├── repository/      # Persistence layer
└── service/         # Business logic
```

## Code improvements implemented

### v1.1.0 - Refactoring and Clean Code

**Problem:** Repeated code and generic exceptions

**Solution:**
- Created specific exception `IncidentNotFoundException` for better semantics
- Extracted method `findIncidentOrThrow()` to eliminate code duplication (DRY)
- Extracted method `validateTransition()` to centralize state validations
- Improved error handling with correct HTTP codes (404 vs 500)
- CORS configured with `allowedOriginPatterns("*")` for development flexibility

**Benefits:**
- Cleaner and more maintainable code
- Less duplication (3 `switchIfEmpty` → 1 reusable method)
- More descriptive and specific errors
- Better development experience with flexible CORS

## Git Workflow

This project uses Git Flow:

- `main` - Código en producción
- `develop` - Rama de desarrollo
- `feature/*` - Nuevas funcionalidades

## Tests

El proyecto incluye tests unitarios y de integración:

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte de cobertura
mvn test jacoco:report
```

**Test coverage:**
- Unit tests for service (IncidentServiceTest)
  - Incident creation
  - Incident retrieval
  - State transitions (acknowledge, resolve)
  - Lifecycle validations
  - Incident deletion
  
- Integration tests for controller (IncidentControllerTest)
  - REST endpoints
  - Input validations
  - HTTP status codes

## Technologies

- Spring Boot 3.2.5
- Spring WebFlux (Reactive programming)
- Spring Data MongoDB Reactive
- MongoDB 7.0
- Project Reactor (Mono/Flux)
- Lombok
- Jakarta Validation
- Maven
- Docker
- JUnit 5 + Mockito (Testing)
