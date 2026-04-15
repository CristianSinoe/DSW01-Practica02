# Quickstart: CRUD de Departamentos

**Generated**: 10 de marzo de 2026  
**Phase 1 Output**: Guía rápida para usar el sistema de gestión de departamentos  
**Audience**: Desarrolladores, administradores, QA

## Overview

Sistema de gestión CRUD para departamentos empresariales con autenticación HTTP Basic y validación de integridad referencial. Implementado como API REST sobre Spring Boot con persistencia PostgreSQL.

## Prerequisites

- Docker & Docker Compose instalados
- Cliente HTTP (curl, Postman, etc.)
- Usuario empleado existente para autenticación
- JDK 17+ (solo para desarrollo)

## Quick Start

### 1. Start the System

```bash
# Clone and start services
git clone <repository>
cd DSW01-Practica02
docker-compose up -d

# Verify services are running
docker-compose ps
```

### 2. Verify API is Running

```bash
# Health check (assuming endpoint exists)
curl -u empleado_clave:password http://localhost:8080/api/v1/departamentos
```

### 3. Basic Operations

#### Create a Department
```bash
curl -X POST http://localhost:8080/api/v1/departamentos \\
  -H "Content-Type: application/json" \\
  -u admin_clave:password \\
  -d '{"nombre":"Recursos Humanos"}'

# Expected Response (201):
# {
#   "id": "550e8400-e29b-41d4-a716-446655440000",
#   "nombre": "Recursos Humanos", 
#   "createdAt": "2026-03-10T10:00:00Z",
#   "updatedAt": "2026-03-10T10:00:00Z"
# }
```

#### List All Departments
```bash
curl -u admin_clave:password http://localhost:8080/api/v1/departamentos

# Expected Response (200):
# {
#   "content": [...departments...],
#   "totalElements": 5,
#   "totalPages": 1,
#   "size": 10,
#   "number": 0
# }
```

#### Get Department by ID
```bash
curl -u admin_clave:password \\
  http://localhost:8080/api/v1/departamentos/550e8400-e29b-41d4-a716-446655440000

# Expected Response (200): department object
```

#### Update Department
```bash
curl -X PUT http://localhost:8080/api/v1/departamentos/550e8400-e29b-41d4-a716-446655440000 \\
  -H "Content-Type: application/json" \\
  -u admin_clave:password \\
  -d '{"nombre":"Recursos Humanos y Cultura"}'

# Expected Response (200): updated department object
```

#### Delete Department
```bash
curl -X DELETE \\
  -u admin_clave:password \\
  http://localhost:8080/api/v1/departamentos/550e8400-e29b-41d4-a716-446655440000

# Expected Response (204): No Content (success)
```

## Common Use Cases

### Pagination & Sorting
```bash
# Get second page, 5 departments per page, sorted by name descending
curl -u admin_clave:password \\
  "http://localhost:8080/api/v1/departamentos?page=1&size=5&sort=nombre,desc"
```

### Error Handling Examples

#### Duplicate Name (409 Conflict)
```bash
curl -X POST http://localhost:8080/api/v1/departamentos \\
  -H "Content-Type: application/json" \\
  -u admin_clave:password \\
  -d '{"nombre":"Existing Department Name"}'

# Response (409):
# {
#   "error": "Conflict",
#   "message": "Department name already exists",
#   "timestamp": "2026-03-10T10:00:00Z"
# }
```

#### Invalid UUID (400 Bad Request)  
```bash
curl -u admin_clave:password \\
  http://localhost:8080/api/v1/departamentos/invalid-uuid

# Response (400):
# {
#   "error": "Bad Request", 
#   "message": "Invalid UUID format",
#   "timestamp": "2026-03-10T10:00:00Z"
# }
```

#### Delete Department with Employees (409 Conflict)
```bash
curl -X DELETE -u admin_clave:password \\
  http://localhost:8080/api/v1/departamentos/dept-with-employees-id

# Response (409):
# {
#   "error": "Conflict",
#   "message": "Cannot delete department with assigned employees", 
#   "timestamp": "2026-03-10T10:00:00Z"
# }
```

## Authentication

### Required Credentials
- **Username**: `clave` field de un empleado existente 
- **Password**: `password` field del mismo empleado
- **Method**: HTTP Basic Authentication
- **Required Role**: Administrator (for all operations)

### Get Valid Credentials
```bash
# Query existing employee credentials (requires DB access)
# Or use test credentials if available:
# Username: admin, Password: admin123
```

## Development Setup

### Local Development (without Docker)
```bash
# Prerequisites: PostgreSQL running locally, JDK 17+
export DB_HOST=localhost
export DB_PORT=5432  
export DB_NAME=empleados_db
export DB_USER=postgres
export DB_PASSWORD=postgres

# Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw test
```

### Database Console (H2 for testing)
```bash
# If H2 console enabled in test profile:
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
```

## API Documentation

### Swagger UI (if enabled)
- **URL**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Contract Documentation  
- **Detailed API Contract**: [contracts/departments-api.md](contracts/departments-api.md)
- **Data Model**: [data-model.md](data-model.md)
- **Implementation Plan**: [plan.md](plan.md)

## Troubleshooting

### Common Issues

**Issue**: "403 Forbidden" 
**Solution**: Verify employee has administrator role and correct credentials

**Issue**: "Connection refused"
**Solution**: Ensure PostgreSQL container is running: `docker-compose ps`

**Issue**: "Department not found (404)"  
**Solution**: Verify UUID format and department exists: check with GET /api/v1/departamentos

**Issue**: "Flyway migration failed"
**Solution**: Check database state, drop and recreate if in development: `docker-compose down -v && docker-compose up -d`

### Logs & Debugging
```bash
# View application logs
docker-compose logs app

# View database logs  
docker-compose logs db

# Follow live logs
docker-compose logs -f app
```

## Next Steps

- Explore [data-model.md](data-model.md) for detailed entity relationships
- Review [contracts/departments-api.md](contracts/departments-api.md) for complete API specification  
- Check employee management API for managing department assignments
- Set up monitoring and metrics for production deployment