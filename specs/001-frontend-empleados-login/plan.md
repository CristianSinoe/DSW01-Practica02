# Implementation Plan: Frontend CRUD de Empleados con Login y Roles

**Branch**: `001-frontend-empleados-login` | **Date**: 13 de marzo de 2026 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-frontend-empleados-login/spec.md`

## Summary

Implementar una interfaz web Angular 20 LTS para login por `email + password` y gestión de empleados con control de acceso por roles: `superusuario/admin` con CRUD completo y `empleado` con permisos de lectura. En backend se ajustará el dominio de `Empleado` para soportar autenticación por email, roles y bootstrap automático de un admin inicial desde variables de entorno cuando no exista ninguno. La solución debe mantener compatibilidad con Spring Boot 3 + Java 17, PostgreSQL/Flyway y ejecución reproducible con Docker Compose.

## Technical Context

**Language/Version**: Backend Java 17 + Spring Boot 3.2.3; Frontend TypeScript 5 + Angular 20 LTS  
**Primary Dependencies**: Spring Web, Spring Data JPA, Spring Security, Spring Validation, Flyway, PostgreSQL, springdoc-openapi; Angular Router, Reactive Forms, HttpClient  
**Storage**: PostgreSQL para datos de negocio y sesión de autenticación en cliente (session/local storage según diseño final)  
**Testing**: JUnit 5 + Spring Boot Test + Mockito + Spring Security Test + Testcontainers; pruebas frontend con `ng test` y smoke E2E en Docker Compose  
**Target Platform**: Linux containerizado (API + DB + frontend) y navegadores web modernos
**Project Type**: Aplicación web full stack (API REST monolítica + SPA Angular)  
**Performance Goals**: Login exitoso en <2s p95; operaciones CRUD visibles en UI en <3s p95; listado paginado backend estable  
**Constraints**: Autenticación por email obligatoria, secretos por entorno, hard delete de empleados, admin bootstrap idempotente, sin credenciales hardcodeadas  
**Scale/Scope**: Un módulo frontend (auth + empleados) y ajustes backend sobre entidad `Empleado`, seguridad y contratos API

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Pre-Phase 0 Gate Review

- [x] Backend scope remains on Spring Boot 3 + Java 17 with layered architecture.
- [x] Security-by-default is addressed (auth, secret handling, no hardcoded credentials).
- [x] PostgreSQL persistence changes include versioned migrations and realistic tests.
- [x] Docker reproducibility is preserved for all required services.
- [x] API contract changes are reflected in Swagger/OpenAPI documentation.
- [x] If web UI is in scope, frontend uses Angular 20 LTS with clear `core/shared/features` boundaries.

**Gate Result**: PASS

## Project Structure

### Documentation (this feature)

```text
specs/001-frontend-empleados-login/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── auth-api.md
│   └── employees-api.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/com/dsw/practica02/empleados/
├── config/
│   ├── SecurityConfig.java
│   ├── BootstrapEmpleadoAuthorizationManager.java
│   └── BootstrapAdminInitializer.java                # new
├── controller/
│   ├── EmpleadoController.java
│   └── AuthController.java                           # new
├── domain/
│   └── Empleado.java
├── dto/
│   ├── EmpleadoCreateRequest.java
│   ├── EmpleadoUpdateRequest.java
│   ├── EmpleadoResponse.java
│   ├── LoginRequest.java                             # new
│   └── LoginResponse.java                            # new
├── repository/
│   └── EmpleadoRepository.java
└── service/
    ├── EmpleadoService.java
    └── AuthService.java                              # new

src/main/resources/
├── application.properties
├── application-dev.properties
├── application-test.properties
└── db/migration/
    ├── V1__create_empleados_table.sql
    ├── V2__add_password_to_empleados.sql
    ├── V3__create_departamentos_and_fk_empleados.sql
    └── V4__add_email_role_to_empleados.sql           # new

src/test/java/com/dsw/practica02/empleados/
├── controller/
├── service/
└── AbstractIntegrationTest.java

frontend/
├── package.json
├── angular.json
└── src/
    ├── app/
    │   ├── core/
    │   │   ├── auth/
    │   │   ├── guards/
    │   │   └── interceptors/
    │   ├── features/
    │   │   ├── auth/
    │   │   └── empleadas/
    │   └── shared/
    ├── assets/
    └── environments/
```

**Structure Decision**: Se mantiene backend monolítico existente en `src/main/...` y se incorpora un frontend Angular 20 LTS en `frontend/` para cumplir la constitución full stack sin romper la estructura actual del proyecto.

## Post-Design Constitution Check

- [x] Stack backend y frontend alineados con principios I y VI.
- [x] Seguridad definida con autenticación por email, RBAC y bootstrap admin por entorno.
- [x] Cambios de datos cubiertos con migración versionada para `email` y `role`.
- [x] Flujo Docker previsto para API, DB y frontend.
- [x] Contratos API definidos para auth y CRUD de empleados.

**Post-Design Gate Result**: PASS

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
