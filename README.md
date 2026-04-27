# Backend Incident Tracker

API REST reactiva para gestión de incidentes en tiempo real usando Spring WebFlux.

## Requisitos

- Java 17+
- Maven 3.6+

## Ejecución

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/incidents` | Listar todos los incidentes |
| GET | `/incidents/stream` | Stream SSE de nuevos incidentes |
| POST | `/incidents` | Crear un nuevo incidente |
| GET | `/incidents/{id}` | Obtener incidente por ID |
| PATCH | `/incidents/{id}/acknowledge` | Marcar como ACKNOWLEDGED |
| PATCH | `/incidents/{id}/resolve` | Marcar como RESOLVED |
| DELETE | `/incidents/{id}` | Eliminar (solo si está RESOLVED) |

## Modelo de datos

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

### Ciclo de vida

```
OPEN → ACKNOWLEDGED → RESOLVED
```

- Un incidente no puede volver a OPEN una vez ACKNOWLEDGED
- Un incidente no puede modificarse si ya está RESOLVED
- Solo se pueden eliminar incidentes RESOLVED

## Ejemplos de uso

Ver archivo `incidents.http` para ejemplos de todas las operaciones.

## Estructura del proyecto

```
src/main/java/com/incidenttracker/
├── config/          # Configuración (CORS)
├── controller/      # Controladores REST
├── dto/             # DTOs (Request/Response)
├── exception/       # Excepciones y manejadores
├── model/           # Entidades de dominio
├── repository/      # Capa de persistencia
└── service/         # Lógica de negocio
```
