# Feature Specification: Frontend CRUD de Empleados con Login

**Feature Branch**: `001-frontend-empleados-login`  
**Created**: 13 de marzo de 2026  
**Status**: Draft  
**Input**: User description: "Frontend que permita un CRUD de Empleados y un login que tome email y pwd para logeo"

## Clarifications

### Session 2026-03-13

- Q: ¿Cuál es el identificador canónico de autenticación para login? → A: Login canónico por `email + password`.
- Q: ¿Qué campos y regla de password aplican al CRUD de empleado? → A: Campos `nombre`, `email`, `telefono`, `direccion`; `password` obligatoria en alta y opcional en edición.
- Q: ¿Qué tipo de eliminación aplica al borrar un empleado? → A: Eliminación definitiva (hard delete).
- Q: ¿Qué permisos por rol aplican al CRUD? → A: `superusuario/admin` con CRUD completo y `empleado` solo puede ver a otros empleados.
- Q: ¿Cómo se crea el `superusuario/admin` inicial? → A: Aprovisionamiento automático al arranque desde parámetros de entorno, solo si no existe.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Iniciar sesión con email y password (Priority: P1)

Como usuaria del sistema, quiero iniciar sesión con email y password para acceder a la gestión de empleados.

**Why this priority**: Sin autenticación, no se puede proteger el acceso al CRUD ni garantizar que solo usuarios válidos administren información.

**Independent Test**: Puede probarse con un conjunto de credenciales válidas e inválidas, verificando acceso exitoso a vistas protegidas y rechazo cuando el login falla.

**Acceptance Scenarios**:

1. **Given** una usuaria registrada con credenciales válidas, **When** ingresa email y password correctos, **Then** accede al área protegida del sistema.
2. **Given** una usuaria que ingresa email o password incorrectos, **When** intenta iniciar sesión, **Then** el sistema deniega el acceso y muestra un mensaje de error.
3. **Given** un despliegue inicial sin `superusuario/admin`, **When** la aplicación arranca con parámetros de entorno válidos para bootstrap, **Then** se crea un único `superusuario/admin` inicial capaz de iniciar sesión.

---

### User Story 2 - Administrar empleados como superusuario/admin (Priority: P1)

Como superusuario/admin autenticada, quiero registrar, consultar, editar y eliminar empleados para mantener actualizado el directorio de personal.

**Why this priority**: El CRUD administrativo es el núcleo de valor de la funcionalidad y depende de permisos elevados.

**Independent Test**: Puede probarse iniciando sesión como admin, realizando alta/consulta/edición/eliminación y verificando que todas las operaciones persisten correctamente.

**Acceptance Scenarios**:

1. **Given** una usuaria con rol `superusuario/admin` autenticada, **When** registra un nuevo empleado con `nombre`, `email`, `telefono`, `direccion` y `password` válidos, **Then** el sistema confirma el alta y lo incluye en el listado.
2. **Given** una usuaria con rol `superusuario/admin` autenticada y un empleado existente, **When** actualiza `nombre`, `email`, `telefono` y `direccion` con datos válidos y decide no enviar `password`, **Then** el sistema guarda los cambios y conserva la contraseña existente.
3. **Given** una usuaria con rol `superusuario/admin` autenticada y un empleado existente, **When** elimina el empleado, **Then** el sistema borra definitivamente el registro y lo remueve del listado.

---

### User Story 3 - Consultar empleados como usuaria estándar (Priority: P2)

Como usuaria autenticada con rol `empleado`, quiero consultar a otros empleados para obtener información del directorio sin poder realizar cambios administrativos.

**Why this priority**: Asegura control de acceso por rol, evitando cambios no autorizados sobre datos de personal.

**Independent Test**: Puede probarse con una usuaria de rol empleado autenticada, validando que puede listar/ver detalle y que crear/editar/eliminar son bloqueados.

**Acceptance Scenarios**:

1. **Given** una usuaria autenticada con rol `empleado`, **When** consulta el listado o detalle de empleados, **Then** el sistema permite la visualización.
2. **Given** una usuaria autenticada con rol `empleado`, **When** intenta crear, editar o eliminar empleados, **Then** el sistema bloquea la acción por permisos insuficientes.

### Edge Cases

- Intento de login con email vacío, password vacía o ambos campos vacíos.
- Intento de login con formato de email inválido.
- Intento de crear un empleado sin `password`.
- Intento de crear un empleado con email ya registrado.
- Intento de editar un empleado sin enviar `password` (debe conservarse la contraseña actual).
- Intento de eliminar un empleado que ya fue eliminado definitivamente.
- Intento de usuaria con rol `empleado` de crear, editar o eliminar empleados.
- Intento de editar o eliminar un empleado que ya no existe.
- Despliegue inicial sin parámetros de entorno válidos para bootstrap de `superusuario/admin`.
- Sesión expirada durante una operación de alta, edición o eliminación.
- Fallo de conectividad con el servicio durante autenticación o acciones CRUD.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST mostrar un formulario de login que solicite email y password.
- **FR-002**: El sistema MUST autenticar a la usuaria con `email + password` antes de permitir acceso al CRUD.
- **FR-003**: El sistema MUST rechazar credenciales inválidas y comunicar el fallo de autenticación de forma clara.
- **FR-004**: El sistema MUST restringir pantallas y acciones según autenticación y rol de la usuaria.
- **FR-005**: El sistema MUST permitir cerrar sesión y revocar el acceso a vistas protegidas.
- **FR-006**: El sistema MUST permitir al rol `superusuario/admin` crear un empleado con `nombre`, `email`, `telefono`, `direccion` y `password`.
- **FR-007**: El sistema MUST permitir a usuarias autenticadas consultar la lista de empleados existentes.
- **FR-008**: El sistema MUST permitir a usuarias autenticadas consultar el detalle de un empleado existente.
- **FR-009**: El sistema MUST permitir solo al rol `superusuario/admin` actualizar `nombre`, `email`, `telefono` y `direccion` de un empleado existente, con `password` opcional.
- **FR-010**: El sistema MUST permitir solo al rol `superusuario/admin` eliminar un empleado existente de forma definitiva (hard delete).
- **FR-011**: El sistema MUST impedir registrar dos empleados con el mismo email.
- **FR-012**: El sistema MUST reflejar en la vista los cambios de alta, edición y eliminación al finalizar cada operación.
- **FR-013**: El sistema MUST validar campos obligatorios según la operación (alta o edición) y formato de email en login y formularios de empleados.
- **FR-014**: El sistema MUST mostrar mensajes de error comprensibles cuando una operación no pueda completarse.
- **FR-015**: El sistema MUST usar `email` como identificador único de autenticación en login, sin usar `clave` como alternativa de acceso.
- **FR-016**: El sistema MUST conservar la contraseña actual cuando una edición de empleado se confirme sin enviar un nuevo valor de `password`.
- **FR-017**: El sistema MUST bloquear con error de autorización los intentos de crear, editar o eliminar realizados por usuarias con rol `empleado`.
- **FR-018**: El sistema MUST aprovisionar automáticamente un único `superusuario/admin` inicial en el arranque cuando no exista ninguno, usando parámetros de entorno de despliegue.

### Constitution Alignment *(mandatory)*

- **CA-001**: El alcance de esta feature MUST mantener compatibilidad con backend Spring Boot 3 + Java 17.
- **CA-002**: Los cambios de login MUST documentar su impacto en autenticación/autorización y manejo de secretos.
- **CA-003**: Si la gestión de empleados requiere cambios de modelo, MUST documentar impacto en migraciones PostgreSQL.
- **CA-004**: Cualquier ajuste en endpoints de login o CRUD MUST actualizar contratos OpenAPI y errores esperados.
- **CA-005**: La UI web MUST delimitar alcance de rutas, componentes, servicios e integración API para Angular 20 LTS.

### Key Entities *(include if feature involves data)*

- **Empleado**: Registro de personal gestionable desde el frontend con `nombre`, `email` (único), `telefono`, `direccion` y `password` (obligatoria en alta, opcional en edición).
- **Credencial de acceso**: Par email/password usado para autenticar el ingreso al módulo.
- **Sesión autenticada**: Estado temporal que habilita o restringe acceso a operaciones CRUD.
- **Rol de acceso**: Clasificación de permisos de usuaria (`superusuario/admin` o `empleado`) que determina operaciones permitidas.

### Dependencies & Assumptions

- Existe un servicio de autenticación que valida `email + password` como credencial canónica.
- Existe un servicio de datos para operaciones de alta, consulta, edición y eliminación de empleados.
- El email es único por empleado en el sistema.
- Existen al menos dos roles de acceso: `superusuario/admin` y `empleado`.
- El despliegue provee parámetros de entorno para bootstrap del `superusuario/admin` inicial.
- El alcance de este feature no incluye recuperación de contraseña ni administración avanzada de roles.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Al menos 95% de usuarias válidas completan login exitoso en menos de 30 segundos.
- **SC-002**: Al menos 90% de usuarias completan una alta de empleado en su primer intento sin asistencia.
- **SC-003**: El 100% de intentos de acceso a CRUD sin sesión autenticada o sin rol autorizado son bloqueados.
- **SC-004**: Al menos 95% de operaciones de edición o eliminación se reflejan en la lista en menos de 3 segundos desde la confirmación.
- **SC-005**: Al menos 90% de pruebas funcionales de punta a punta (login + CRUD completo) finalizan correctamente en menos de 5 minutos por caso.
- **SC-006**: El 100% de despliegues iniciales con parámetros de entorno válidos generan exactamente un `superusuario/admin` utilizable para login.
