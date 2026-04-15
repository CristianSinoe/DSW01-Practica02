# Data Model: Frontend CRUD de Empleados con Login y Roles

## 1) Entidad persistida `Empleado`

| Campo | Tipo | Reglas de validación/negocio |
|---|---|---|
| `id` | UUID | PK, generado por base de datos |
| `clave` | String(100) | Identificador legado interno, único, no expuesto como credencial de login |
| `nombre` | String(100) | Obligatorio, no vacío |
| `email` | String(150) | Obligatorio, formato email válido, único a nivel sistema |
| `telefono` | String(100) | Obligatorio, no vacío |
| `direccion` | String(100) | Obligatoria, no vacía |
| `password` | String(255) | Obligatoria en alta, opcional en edición, siempre codificada |
| `role` | Enum(`ADMIN`, `EMPLEADO`) | Obligatorio; define permisos de CRUD |
| `departamento_id` | UUID nullable | FK opcional a `departamentos.id` (compatibilidad con modelo existente) |
| `created_at` | OffsetDateTime | Timestamp de creación |
| `updated_at` | OffsetDateTime | Timestamp de última actualización |

### Restricciones de integridad

- Unicidad de `email` mediante índice/constraint dedicado.
- Unicidad existente de `clave` preservada por compatibilidad.
- En actualización, si `password` no llega en request, se conserva el hash actual.
- Eliminación de empleado: **hard delete**.

## 2) Modelo de autenticación

### Credencial de login

| Campo | Tipo | Regla |
|---|---|---|
| `email` | String | Identificador canónico de autenticación |
| `password` | String | Debe corresponder con hash persistido |

### Sesión autenticada (cliente)

| Campo | Tipo | Regla |
|---|---|---|
| `principalEmail` | String | Email autenticado |
| `role` | Enum(`ADMIN`, `EMPLEADO`) | Permisos activos para guards/UI |
| `authHeader` | String | Representación de credencial para solicitudes autenticadas |

> La sesión de frontend no sustituye la autenticación backend; solo representa estado de cliente.

## 3) Bootstrap de superusuario/admin

### Fuente de datos de bootstrap (entorno)

| Variable | Uso |
|---|---|
| `ADMIN_EMAIL` | Email del admin inicial |
| `ADMIN_PASSWORD` | Password del admin inicial (se codifica antes de persistir) |
| `ADMIN_NOMBRE` | Nombre visible del admin inicial |
| `ADMIN_TELEFONO` | Teléfono inicial del admin |
| `ADMIN_DIRECCION` | Dirección inicial del admin |

### Regla de transición

- Si al arranque no existe ningún `Empleado` con rol `ADMIN`, se crea uno con datos de entorno.
- Si ya existe un `ADMIN`, el bootstrap no crea duplicados (idempotente).

## 4) Reglas de autorización por rol

| Operación | ADMIN | EMPLEADO |
|---|---|---|
| Login | ✅ | ✅ |
| Listar empleados | ✅ | ✅ |
| Ver detalle de empleado | ✅ | ✅ |
| Crear empleado | ✅ | ❌ (403) |
| Editar empleado | ✅ | ❌ (403) |
| Eliminar empleado (hard delete) | ✅ | ❌ (403) |

## 5) Transiciones de estado relevantes

### Ciclo de vida de empleado

1. `inexistente` → `activa` (alta por ADMIN).
2. `activa` → `activa` (edición por ADMIN, con o sin cambio de password).
3. `activa` → `eliminada` (hard delete por ADMIN).

### Flujo de autenticación

1. `no autenticada` → `autenticada` (credenciales válidas).
2. `autenticada` → `no autenticada` (logout o expiración de sesión en cliente).

## 6) Implicaciones de migración

- Nueva migración versionada para `email` y `role` en tabla `empleados`.
- Backfill de datos legacy para cumplir restricciones `NOT NULL`/unicidad de `email`.
- Ajuste de índices para búsqueda eficiente por `email` (login).
