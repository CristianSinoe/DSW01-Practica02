---
description: "Task list for feature implementation"
---

# Tasks: Frontend CRUD de Empleados con Login y Roles

**Input**: Design documents from /specs/001-frontend-empleados-login/
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Not requested for this feature.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: [ID] [P?] [Story] Description

- [P]: Can run in parallel (different files, no dependencies)
- [Story]: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and base structure

- [X] T001 Initialize Angular 20 workspace in frontend/ with frontend/package.json, frontend/angular.json, frontend/tsconfig.json, frontend/src/main.ts
- [X] T002 Create base app shell and routing config in frontend/src/app/app.component.ts, frontend/src/app/app.routes.ts, frontend/src/app/app.config.ts
- [X] T003 [P] Add environment config for API base URL in frontend/src/environments/environment.ts, frontend/src/environments/environment.prod.ts
- [X] T004 [P] Add frontend container build config in frontend/Dockerfile, frontend/.dockerignore
- [X] T005 Update docker compose to include frontend service and API base URL env in docker-compose.yml

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that must be complete before any user story can be implemented

- [X] T006 Add Flyway migration for email and role columns with constraints in src/main/resources/db/migration/V4__add_email_role_to_empleados.sql
- [X] T007 Update Empleado entity with email and role fields in src/main/java/com/dsw/practica02/empleados/domain/Empleado.java
- [X] T008 [P] Add email lookup helpers in src/main/java/com/dsw/practica02/empleados/repository/EmpleadoRepository.java
- [X] T009 [P] Update DTOs for email, role, and password optional on update in src/main/java/com/dsw/practica02/empleados/dto/EmpleadoCreateRequest.java, src/main/java/com/dsw/practica02/empleados/dto/EmpleadoUpdateRequest.java, src/main/java/com/dsw/practica02/empleados/dto/EmpleadoResponse.java
- [X] T010 Update email based auth and role authorities in src/main/java/com/dsw/practica02/empleados/config/SecurityConfig.java, src/main/java/com/dsw/practica02/empleados/config/BootstrapEmpleadoAuthorizationManager.java
- [X] T011 Add bootstrap admin initializer using ADMIN_* env vars in src/main/java/com/dsw/practica02/empleados/config/BootstrapAdminInitializer.java

---

## Phase 3: User Story 1 - Iniciar sesion con email y password (Priority: P1) MVP

**Goal**: Authenticate with email and password and establish a session for protected UI

**Independent Test**: Using valid Basic credentials, /api/v1/auth/me returns 200 and the login UI grants access; invalid credentials show an error

### Implementation for User Story 1

- [X] T012 [P] [US1] Add auth DTOs in src/main/java/com/dsw/practica02/empleados/dto/LoginRequest.java, src/main/java/com/dsw/practica02/empleados/dto/LoginResponse.java
- [X] T013 [US1] Implement AuthService in src/main/java/com/dsw/practica02/empleados/service/AuthService.java
- [X] T014 [US1] Implement AuthController GET /api/v1/auth/me in src/main/java/com/dsw/practica02/empleados/controller/AuthController.java
- [X] T015 [P] [US1] Add auth session models and storage in frontend/src/app/core/auth/auth.types.ts, frontend/src/app/core/auth/auth.storage.ts
- [X] T016 [US1] Implement frontend AuthService in frontend/src/app/core/auth/auth.service.ts
- [X] T017 [P] [US1] Implement auth interceptor in frontend/src/app/core/interceptors/auth.interceptor.ts
- [X] T018 [US1] Implement auth guard in frontend/src/app/core/guards/auth.guard.ts
- [X] T019 [US1] Build login component in frontend/src/app/features/auth/login.component.ts, frontend/src/app/features/auth/login.component.html, frontend/src/app/features/auth/login.component.css
- [X] T020 [US1] Wire auth routes and providers in frontend/src/app/app.routes.ts, frontend/src/app/app.config.ts, frontend/src/app/app.component.ts

---

## Phase 4: User Story 2 - Administrar empleados como admin (Priority: P1)

**Goal**: Admin can create, edit, delete, and view empleados

**Independent Test**: Login as admin, create an empleado, edit without password, and hard delete; list reflects changes

### Implementation for User Story 2

- [X] T021 [US2] Update EmpleadoService rules for create/update/delete in src/main/java/com/dsw/practica02/empleados/service/EmpleadoService.java
- [X] T022 [US2] Update EmpleadoController CRUD and ADMIN authorization in src/main/java/com/dsw/practica02/empleados/controller/EmpleadoController.java
- [X] T023 [P] [US2] Add empleadas models in frontend/src/app/features/empleadas/empleadas.models.ts
- [X] T024 [US2] Implement Empleadas API service in frontend/src/app/features/empleadas/empleadas.service.ts
- [X] T025 [US2] Implement empleadas list UI in frontend/src/app/features/empleadas/empleadas-list.component.ts, frontend/src/app/features/empleadas/empleadas-list.component.html, frontend/src/app/features/empleadas/empleadas-list.component.css
- [X] T026 [US2] Implement empleadas form UI in frontend/src/app/features/empleadas/empleadas-form.component.ts, frontend/src/app/features/empleadas/empleadas-form.component.html, frontend/src/app/features/empleadas/empleadas-form.component.css
- [X] T027 [US2] Implement empleadas detail UI in frontend/src/app/features/empleadas/empleadas-detail.component.ts, frontend/src/app/features/empleadas/empleadas-detail.component.html, frontend/src/app/features/empleadas/empleadas-detail.component.css
- [X] T028 [US2] Add empleadas routes and navigation in frontend/src/app/features/empleadas/empleadas.routes.ts, frontend/src/app/app.routes.ts
- [X] T029 [US2] Handle CRUD error states (400/404/409) in frontend/src/app/features/empleadas/empleadas.service.ts, frontend/src/app/features/empleadas/empleadas-list.component.ts, frontend/src/app/features/empleadas/empleadas-form.component.ts, frontend/src/app/features/empleadas/empleadas-detail.component.ts

---

## Phase 5: User Story 3 - Consultar empleados como usuaria estandar (Priority: P2)

**Goal**: Empleada can view list and detail but cannot mutate records

**Independent Test**: Login as EMPLEADO and confirm list/detail access while create/edit/delete is blocked

### Implementation for User Story 3

- [X] T030 [US3] Add role guard for admin only routes in frontend/src/app/core/guards/role.guard.ts, frontend/src/app/features/empleadas/empleadas.routes.ts
- [X] T031 [US3] Hide admin actions and show read only note in frontend/src/app/features/empleadas/empleadas-list.component.html, frontend/src/app/features/empleadas/empleadas-detail.component.html
- [X] T032 [US3] Show permission messaging on 403 responses in frontend/src/app/features/empleadas/empleadas-list.component.ts, frontend/src/app/features/empleadas/empleadas-form.component.ts, frontend/src/app/features/empleadas/empleadas-detail.component.ts

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T033 [P] Align OpenAPI docs with contracts in src/main/java/com/dsw/practica02/empleados/controller/AuthController.java, src/main/java/com/dsw/practica02/empleados/controller/EmpleadoController.java, specs/001-frontend-empleados-login/contracts/auth-api.md, specs/001-frontend-empleados-login/contracts/employees-api.md
- [X] T034 [P] Validate docker compose and quickstart docs in docker-compose.yml, specs/001-frontend-empleados-login/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- Setup (Phase 1): No dependencies - can start immediately
- Foundational (Phase 2): Depends on Setup completion - blocks all user stories
- User Stories (Phase 3+): Depend on Foundational completion
- Polish (Final Phase): Depends on all targeted user stories being complete

### User Story Dependencies

- User Story 1 (P1): Can start after Foundational - enables authenticated UI
- User Story 2 (P1): Can start after Foundational; end to end testing depends on User Story 1
- User Story 3 (P2): Can start after Foundational; end to end testing depends on User Story 1

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration

### Parallel Opportunities

- Setup: T003 and T004 can run in parallel after T001
- Foundational: T008 and T009 can run in parallel after T006 and T007
- US1: T012, T015, and T017 can run in parallel once dependencies are ready
- US2: T023 can run in parallel with backend CRUD updates
- Polish: T033 and T034 can run in parallel

---

## Parallel Example: User Story 1

Task: "Add auth DTOs in src/main/java/com/dsw/practica02/empleados/dto/LoginRequest.java, src/main/java/com/dsw/practica02/empleados/dto/LoginResponse.java"
Task: "Add auth session models and storage in frontend/src/app/core/auth/auth.types.ts, frontend/src/app/core/auth/auth.storage.ts"
Task: "Implement auth interceptor in frontend/src/app/core/interceptors/auth.interceptor.ts"

---

## Parallel Example: User Story 2

Task: "Add empleadas models in frontend/src/app/features/empleadas/empleadas.models.ts"
Task: "Update EmpleadoService rules for create/update/delete in src/main/java/com/dsw/practica02/empleados/service/EmpleadoService.java"

---

## Parallel Example: User Story 3

Task: "Add role guard for admin only routes in frontend/src/app/core/guards/role.guard.ts, frontend/src/app/features/empleadas/empleadas.routes.ts"
Task: "Hide admin actions and show read only note in frontend/src/app/features/empleadas/empleadas-list.component.html, frontend/src/app/features/empleadas/empleadas-detail.component.html"

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. Validate User Story 1 independently
5. Stop and confirm before moving on

### Incremental Delivery

1. Complete Setup + Foundational
2. Add User Story 1 and validate
3. Add User Story 2 and validate
4. Add User Story 3 and validate
5. Finish with Polish tasks

### Parallel Team Strategy

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Polish tasks can run in parallel after story completion
