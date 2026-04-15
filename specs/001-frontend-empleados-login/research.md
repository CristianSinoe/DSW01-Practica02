# Research: Frontend CRUD de Empleados con Login y Roles

## Decision 1: Mantener protección API con HTTP Basic y login por `email + password`

- **Decision**: Mantener Spring Security con HTTP Basic para endpoints protegidos, reemplazando lookup por `clave` a lookup por `email` y exponiendo un endpoint de verificación de sesión (`/api/v1/auth/me`) para inicializar estado de UI.
- **Rationale**: Cumple el principio constitucional de seguridad por defecto y minimiza cambios disruptivos en una base backend ya operativa.
- **Alternatives considered**:
  - Migrar a JWT completo en esta feature: descartado por mayor alcance y cambios transversales (token lifecycle, refresh, revocación) no necesarios para MVP.
  - Mantener login por `clave`: descartado por contradicción directa con aclaración funcional (`email + password`).

## Decision 2: Modelo de roles en entidad `Empleado`

- **Decision**: Añadir atributo `role` en `Empleado` con valores `ADMIN` y `EMPLEADO`; las reglas de autorización se aplican en backend (CRUD solo `ADMIN`, lectura para autenticadas).
- **Rationale**: Centraliza permisos en el mismo origen de identidad, evita tablas adicionales y simplifica trazabilidad.
- **Alternatives considered**:
  - Tabla separada de roles/usuarios: descartado por sobrecomplejidad para este alcance.
  - Rol único para todas las usuarias: descartado por incumplir requisito de permisos diferenciados.

## Decision 3: Migración de datos para `email` único y `role`

- **Decision**: Crear migración `V4` para agregar columnas `email` (único) y `role`; backfill controlado para registros existentes con estrategia determinista y validaciones de unicidad.
- **Rationale**: Flyway mantiene versionado reproducible y evita ruptura de despliegues por datos legacy.
- **Alternatives considered**:
  - Requiere limpieza manual previa de datos: descartado por riesgo operativo y baja reproducibilidad.
  - Crear tabla nueva de usuarias y duplicar identidad: descartado por deuda de sincronización.

## Decision 4: Bootstrap de `superusuario/admin` por entorno e idempotente

- **Decision**: Implementar inicializador de arranque que cree un único `ADMIN` desde variables de entorno (`ADMIN_EMAIL`, `ADMIN_PASSWORD`, datos base) solo cuando no exista.
- **Rationale**: Garantiza operación inicial en Docker/local y evita hardcode de secretos.
- **Alternatives considered**:
  - Promover automáticamente el primer registro de usuaria: descartado por riesgo de escalamiento accidental de privilegios.
  - Inserción manual en DB: descartado por romper automatización de despliegue.

## Decision 5: Arquitectura Angular 20 LTS por dominios

- **Decision**: Estructurar frontend en `core/shared/features`, con `AuthService`, interceptores para `Authorization`, guards de rutas y módulos/standalone features para `auth` y el módulo de empleados (implementado actualmente bajo `features/empleadas`).
- **Rationale**: Alineado con constitución y escalable para nuevas historias.
- **Alternatives considered**:
  - Estructura plana por tipo de archivo: descartada por baja mantenibilidad.
  - Mezclar lógica de auth en componentes: descartado por acoplamiento y dificultad de pruebas.

## Decision 6: Integración Docker full stack

- **Decision**: Extender `docker-compose.yml` con servicio `frontend` y variable de entorno para URL de API, manteniendo `api` + `db` como base.
- **Rationale**: Cumple el principio de entorno reproducible para todo el stack.
- **Alternatives considered**:
  - Ejecutar frontend solo con `ng serve` manual: descartado por inconsistencia entre entornos.
  - Servir frontend desde JAR backend: descartado por acoplamiento de ciclos de build.

## Decision 7: Contratos API explícitos para auth y empleados

- **Decision**: Definir contratos separados `auth-api.md` y `employees-api.md`, incluyendo permisos por rol, errores esperados y ejemplos request/response.
- **Rationale**: Reduce ambigüedad frontend-backend y cumple gobernanza de API contratada.
- **Alternatives considered**:
  - Un único contrato informal en `quickstart`: descartado por falta de precisión para implementación.
  - Confiar solo en Swagger sin contrato de feature: descartado por pérdida de contexto funcional.
