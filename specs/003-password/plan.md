# Implementation Plan: Ajustes Password Empleado

**Branch**: `003-password` | **Date**: 2026-03-10 | **Spec**: `/specs/003-password/spec.md`
**Input**: Feature specification from `/specs/003-password/spec.md`

## Summary

Consolidar los ajustes del CRUD de empleados para exigir contraseña en altas y actualizaciones, autenticar con `clave/password` de empleados existentes y formalizar el contrato de listado paginado con valores por defecto, límite máximo de tamaño y reglas de rechazo para parámetros inválidos. El plan prioriza consistencia con Spring Boot 3 + Java 17, seguridad por defecto y documentación contractual verificable.

## Technical Context

**Language/Version**: Java 17, Spring Boot 3.2.3  
**Primary Dependencies**: Spring Web, Spring Data JPA, Spring Security, Spring Validation, Flyway, PostgreSQL driver, springdoc-openapi  
**Storage**: PostgreSQL con migraciones Flyway versionadas (`V1`, `V2`, `V3`)  
**Testing**: JUnit 5 + Spring Boot Test + Mockito + Spring Security Test + Testcontainers (PostgreSQL)  
**Target Platform**: API backend en Linux/containers con Docker Compose  
**Project Type**: Web-service REST monolítico por capas (`controller/service/repository/domain/dto`)  
**Performance Goals**: Listado de empleados siempre paginado, con `size` por defecto 10 y tope 100 para evitar respuestas masivas  
**Constraints**: HTTP Basic obligatorio para recursos protegidos, contraseña nunca en texto plano, bootstrap público de alta solo cuando no existen empleados  
**Scale/Scope**: Feature acotado al módulo de empleados y su autenticación; compatibilidad con CRUD existente y relación opcional con departamentos

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Gate Review (pre-Phase 0)

- **I. Stack Tecnológico Innegociable**: **PASS**. Se mantiene Spring Boot 3 + Java 17 y arquitectura por capas existente.
- **II. Seguridad por Defecto**: **PASS**. El diseño mantiene HTTP Basic y define tratamiento explícito para bootstrap inicial y credenciales inválidas.
- **III. Persistencia con PostgreSQL**: **PASS**. Se reutiliza PostgreSQL con migraciones versionadas (incluida migración de password ya existente).
- **IV. Entorno Reproducible con Docker**: **PASS**. El proyecto conserva ejecución estándar con `docker-compose.yml` (servicios `api` + `db`).
- **V. API Contratada y Documentada**: **PASS**. Este plan entrega contrato de API, modelo de datos y quickstart verificable.

**Resultado de gate pre-research**: **PASS**

### Gate Review (post-Phase 1 design)

- **I. Stack Tecnológico Innegociable**: **PASS**. Artefactos de diseño no introducen librerías ni patrones fuera del stack aprobado.
- **II. Seguridad por Defecto**: **PASS**. Contrato y modelo exigen almacenamiento codificado y control de acceso coherente con HTTP Basic.
- **III. Persistencia con PostgreSQL**: **PASS**. Modelo y contratos preservan persistencia relacional y trazabilidad por migraciones.
- **IV. Entorno Reproducible con Docker**: **PASS**. Quickstart documenta flujo ejecutable en entorno local reproducible.
- **V. API Contratada y Documentada**: **PASS**. Se documentan endpoints, parámetros, errores y restricciones del feature.

**Resultado de gate post-design**: **PASS**

## Project Structure

### Documentation (this feature)

```text
specs/003-password/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── employees-api.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/com/dsw/practica02/empleados/
├── config/
│   ├── EmpleadoFeatureProperties.java
│   ├── BootstrapEmpleadoAuthorizationManager.java
│   ├── SecurityConfig.java
│   ├── ApiError.java
│   └── GlobalExceptionHandler.java
├── controller/
│   └── EmpleadoController.java
├── domain/
│   └── Empleado.java
├── dto/
│   ├── EmpleadoCreateRequest.java
│   ├── EmpleadoUpdateRequest.java
│   ├── EmpleadoResponse.java
│   └── EmpleadoMapper.java
├── repository/
│   └── EmpleadoRepository.java
└── service/
    ├── EmpleadoService.java
    └── exception/
        ├── InvalidPaginationException.java
        └── PasswordEncodingException.java

src/main/resources/
├── application.properties
├── application-dev.properties
├── application-test.properties
└── db/migration/
    ├── V1__create_empleados_table.sql
    ├── V2__add_password_to_empleados.sql
    └── V3__create_departamentos_and_fk_empleados.sql

src/test/java/com/dsw/practica02/empleados/
├── controller/
├── service/
└── AbstractIntegrationTest.java
```

**Structure Decision**: Se mantiene un único backend Spring Boot con separación por capas y pruebas separadas por tipo (servicio e integración/controlador).

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
