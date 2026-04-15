# Feature Specification: Ajustes Password Empleado

**Feature Branch**: `003-password`  
**Created**: 10 de marzo de 2026  
**Status**: Draft  
**Input**: User description: "Necesito actualizar el CRUD existente para la entidad Empleado. Por favor, revisa lo siguiente los siguientes ajustes en el código basándote en estas especificaciones (si ya existen los cambios solo ajustalos y agrega la documentación): 1. Modificar la Entidad: Agrega un campo de contraseña... 2. Autenticación... 3. Paginación..."

## Clarifications

### Session 2026-03-10

- Q: ¿Cuál debe ser la política de bootstrap para `POST /api/v1/empleados`? → A: Público solo cuando el total de empleados es 0; después requiere autenticación.
- Q: ¿Qué modelo de roles aplica para operar el CRUD de empleados? → A: Todos los empleados autenticados se tratan como `ADMIN` para este feature.
- Q: ¿Cómo manejar un `size` mayor al máximo permitido en el listado paginado? → A: Limitar `size` a un máximo de 100 recortando el valor solicitado.
- Q: ¿Cómo manejar `page < 0` o `size <= 0` en paginación? → A: Responder `400 Bad Request`.
- Q: ¿Cómo debe persistirse `password` en la entidad Empleado? → A: Solo codificada; nunca en texto plano.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Gestionar contraseña de empleado (Priority: P1)

Como administrador, quiero crear y actualizar empleados con contraseña para que cada empleado tenga credenciales válidas de acceso.

**Why this priority**: Sin contraseña persistida no existe base para autenticación y el control de acceso queda incompleto.

**Independent Test**: Crear y actualizar un empleado enviando `password`; validar que la operación sea exitosa y que el campo no se exponga en respuestas.

**Acceptance Scenarios**:

1. **Given** que no existe un empleado con la clave enviada, **When** creo un empleado con contraseña válida, **Then** el sistema guarda el empleado y confirma creación.
2. **Given** que existe un empleado, **When** actualizo sus datos con una nueva contraseña válida, **Then** el sistema persiste el cambio y devuelve el recurso actualizado sin incluir contraseña.
3. **Given** una solicitud de alta o actualización con contraseña vacía o fuera de longitud permitida, **When** envío la operación, **Then** el sistema la rechaza por validación.
4. **Given** una solicitud válida con contraseña en texto plano, **When** el sistema persiste el empleado, **Then** almacena la contraseña en forma codificada y no conserva el valor plano.

---

### User Story 2 - Autenticación por entidad Empleado (Priority: P1)

Como sistema, quiero autenticar usuarios contra los registros de empleados para que el acceso dependa de credenciales reales y vigentes.

**Why this priority**: Es un requisito crítico de seguridad; la autenticación debe apoyarse en datos de negocio reales.

**Independent Test**: Crear un empleado y acceder con sus credenciales a un endpoint protegido; validar acceso permitido con credenciales correctas y rechazo con credenciales inválidas.

**Acceptance Scenarios**:

1. **Given** un empleado existente con contraseña válida, **When** invoca un endpoint protegido usando sus credenciales, **Then** el sistema autoriza la solicitud.
2. **Given** credenciales inválidas o empleado inexistente, **When** se intenta acceder a un endpoint protegido, **Then** el sistema rechaza la solicitud.
3. **Given** que aún no existe primer empleado, **When** se invoca el endpoint público de alta inicial, **Then** el sistema permite el bootstrap del primer usuario.
4. **Given** que ya existe al menos un empleado, **When** se intenta usar `POST /api/v1/empleados` sin autenticación, **Then** el sistema rechaza la solicitud.

---

### User Story 3 - Listar empleados con paginación (Priority: P2)

Como administrador, quiero consultar empleados de forma paginada para evitar respuestas masivas y mejorar la usabilidad del listado.

**Why this priority**: Reduce carga, mejora tiempos percibidos y hace escalable la consulta de datos.

**Independent Test**: Crear múltiples empleados y consultar `GET /empleados` con `page`, `size` y `sort`; validar metadatos y contenido esperado.

**Acceptance Scenarios**:

1. **Given** que existen múltiples empleados, **When** consulto el listado sin parámetros, **Then** el sistema devuelve una página con tamaño y orden por defecto.
2. **Given** parámetros de paginación y orden válidos, **When** consulto el listado, **Then** el sistema devuelve el subconjunto solicitado con metadatos de paginación.
3. **Given** que no existen empleados, **When** consulto el listado, **Then** el sistema responde exitosamente con página vacía y metadatos coherentes.
4. **Given** una solicitud de listado con `size` mayor al límite, **When** se procesa la petición, **Then** el sistema aplica `size=100` y retorna resultados paginados.
5. **Given** una solicitud de listado con `page < 0` o `size <= 0`, **When** se procesa la petición, **Then** el sistema responde `400 Bad Request`.

### Edge Cases

- Alta o actualización con `password` vacía, nula o con longitud fuera de rango.
- Alta o actualización con `password` válida: el valor recibido debe persistirse codificado y no en texto plano.
- Intento de autenticación con `clave` correcta pero contraseña incorrecta.
- Solicitud de listado con `size` mayor al máximo permitido: el sistema recorta a 100.
- Solicitud de listado con `page < 0` o `size <= 0`: el sistema rechaza con `400 Bad Request`.
- Listado con `sort` no informado: se aplica el orden por defecto.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE incluir un campo `password` en la entidad de empleado y persistirlo en base de datos en forma codificada.
- **FR-002**: El sistema DEBE requerir `password` en las operaciones de alta y actualización de empleado.
- **FR-003**: El sistema DEBE impedir exponer `password` en las respuestas de consulta de empleados.
- **FR-004**: El sistema DEBE autenticar accesos protegidos usando `clave` y `password` de empleados existentes.
- **FR-005**: El sistema DEBE rechazar credenciales inválidas al consumir endpoints protegidos.
- **FR-006**: El sistema DEBE permitir el alta inicial pública de empleado solo cuando no exista ningún empleado registrado.
- **FR-011**: Cuando ya exista al menos un empleado, `POST /api/v1/empleados` sin autenticación DEBE responder `401 Unauthorized`.
- **FR-012**: El sistema DEBE autorizar operaciones del CRUD de empleados tratando a todo empleado autenticado como `ADMIN` en este alcance.
- **FR-007**: El endpoint de listado de empleados DEBE soportar paginación.
- **FR-008**: El listado paginado DEBE aceptar parámetros de página, tamaño y orden.
- **FR-009**: El listado DEBE devolver metadatos de paginación además de los elementos.
- **FR-010**: La documentación del API de empleados DEBE describir claramente autenticación por empleado, validación de contraseña y comportamiento paginado.
- **FR-013**: El listado DEBE aplicar un tamaño máximo de página de 100 registros recortando cualquier valor superior solicitado en `size`.
- **FR-014**: El listado DEBE rechazar con `400 Bad Request` cuando `page < 0` o `size <= 0`.
- **FR-015**: El sistema DEBE rechazar la operación de alta/actualización y registrar error cuando falle el proceso de codificación de `password`.

### Key Entities *(include if feature involves data)*

- **Empleado**: Recurso con identidad de negocio (`clave`) y credencial (`password`) usado tanto para gestión CRUD como para autenticación.
- **Página de empleados**: Estructura de respuesta de listado que incluye elementos y metadatos de navegación (total, tamaño, número de página).

### Dependencies & Assumptions

- La autenticación de la API sigue esquema HTTP Basic.
- El sistema opera sobre una base de datos relacional con soporte de paginación en consultas.
- El endpoint de alta inicial de empleado es público únicamente durante bootstrap (total de empleados = 0).
- Para este feature no se introduce diferenciación de roles en entidad; todo usuario autenticado opera como `ADMIN`.
- La estrategia de codificación de contraseñas es obligatoria y consistente para alta, actualización y autenticación.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de altas y actualizaciones válidas de empleado se completan incluyendo contraseña obligatoria.
- **SC-002**: El 100% de accesos a endpoints protegidos con credenciales inválidas son rechazados.
- **SC-003**: El 100% de respuestas de empleado omiten el valor de contraseña.
- **SC-004**: El 100% de solicitudes de listado devuelven respuesta paginada con metadatos.
- **SC-005**: En pruebas funcionales, consultas paginadas de empleados evitan devolver el total de registros en una sola respuesta.
- **SC-006**: El 100% de solicitudes de listado con `page < 0` o `size <= 0` reciben `400 Bad Request`.
- **SC-007**: El 100% de contraseñas persistidas en altas/actualizaciones se almacenan codificadas y no en texto plano.
