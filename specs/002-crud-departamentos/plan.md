# Implementation Plan: CRUD de Departamentos

**Branch**: `002-crud-departamentos` | **Date**: 10 de marzo de 2026 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/002-crud-departamentos/spec.md`

**Note**: Este plan documenta la implementación existente y proporciona estructura formal para el CRUD de departamentos ya desarrollado.

## Summary

Sistema de gestión CRUD completo para departamentos empresariales que permite a administradores crear, consultar, actualizar y eliminar departamentos con validación de integridad referencial hacia empleados. Implementado como API REST con Spring Boot siguiendo arquitectura por capas, autenticación HTTP Basic, y persistencia PostgreSQL con migraciones versionadas.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3, Spring Data JPA, Spring Security, Flyway  
**Storage**: PostgreSQL con migraciones versionadas  
**Testing**: JUnit 5, Testcontainers para tests de integración  
**Target Platform**: Linux server containerizado con Docker  
**Project Type**: API REST microservice  
**Performance Goals**: <200ms p95 para operaciones CRUD, soporte 100 operaciones concurrentes  
## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principio | Estado | Justificación |
|-----------|--------|---------------|
| **I. Stack Tecnológico Innegociable** | ✅ PASS | Spring Boot 3 + Java 17 exclusivamente |
| **II. Seguridad por Defecto** | ✅ PASS | HTTP Basic implementado para todos los endpoints |
| **III. Persistencia Consistente PostgreSQL** | ✅ PASS | PostgreSQL + Flyway para migraciones versionadas |
| **IV. Entorno Reproducible Docker** | ✅ PASS | Docker Compose configurado para API + PostgreSQL |
| **V. API Contratada y Documentada** | ✅ PASS | Swagger/OpenAPI + contracts documentados |

**Gates Status**: ✅ ALL PASS - No violations detected

### Post-Design Re-evaluation (Phase 1 Complete)

| Principio | Re-check Status | Design Compliance |
|-----------|-----------------|-------------------|
| **Arquitectura por Capas** | ✅ CONFIRMED | Controller/Service/Repository/Domain separados |
| **Validación Obligatoria** | ✅ CONFIRMED | @Valid en todos los endpoints, DTOs validados |
| **Manejo Centralizado Errores** | ✅ CONFIRMED | GlobalExceptionHandler implementado |
| **Configuración por Perfiles** | ✅ CONFIRMED | dev/test/prod profiles configurados |
| **Logs Estructurados** | ✅ CONFIRMED | Spring Boot logging sin datos sensibles |
| **Versionado API** | ✅ CONFIRMED | /api/v1/ prefix implementado |

**Final Constitution Status**: ✅ **COMPLETE COMPLIANCE** - All constitutional requirements met by design

## Project Structure

### Documentation (this feature)

```text
specs/002-crud-departamentos/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── departments-api.md
└── checklists/
    └── requirements.md
```

### Source Code (repository root)

```text
src/main/java/com/dsw/practica02/empleados/
├── controller/
│   └── DepartamentoController.java    # REST endpoints CRUD
├── service/
│   └── DepartamentoService.java       # Business logic layer
├── repository/
│   └── DepartamentoRepository.java    # Data access layer
├── domain/
│   ├── Departamento.java              # JPA entity
│   └── Empleado.java                  # Related entity with FK
├── dto/
│   ├── DepartamentoCreateRequest.java # Request DTOs
│   ├── DepartamentoUpdateRequest.java
│   ├── DepartamentoResponse.java      # Response DTO
│   └── DepartamentoMapper.java        # Entity-DTO mapping
└── config/
    ├── SecurityConfig.java            # HTTP Basic config
    └── GlobalExceptionHandler.java    # Centralized errors

src/main/resources/
├── application.properties             # Base configuration
├── application-dev.properties         # Dev profile
├── application-test.properties        # Test profile
└── db/migration/
    ├── V1__create_empleados_table.sql
    ├── V2__add_password_to_empleados.sql
    └── V3__create_departamentos_and_fk_empleados.sql

src/test/java/com/dsw/practica02/empleados/
├── controller/
│   └── DepartamentoControllerTest.java
├── service/  
│   └── DepartamentoServiceTest.java
└── AbstractIntegrationTest.java       # Testcontainers base
```

**Structure Decision**: Arquitectura por capas Spring Boot estándar con separación clara controller/service/repository/domain. Integración completa con el módulo de empleados existente mediante foreign key y validación de integridad referencial.

## Complexity Tracking

> **No violations detected - Constitutional compliance achieved**

| Aspect | Implementation | Constitution Alignment |
|--------|----------------|------------------------|
| Security | HTTP Basic + role-based authorization | ✅ Seguridad por defecto |
| Persistence | PostgreSQL + Flyway migrations | ✅ Persistencia consistente |
| Architecture | Controller/Service/Repository layers | ✅ Stack tecnológico |
| Documentation | Swagger + API contracts documented | ✅ API contratada |
| Environment | Docker Compose ready | ✅ Entorno reproducible |
