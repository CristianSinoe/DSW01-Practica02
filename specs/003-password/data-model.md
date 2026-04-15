# Data Model: Password, Auth & Pagination en Empleado

## 1) Entidad persistida `Empleado`

| Campo | Tipo | Reglas de validación/negocio |
|------|------|-------------------------------|
| `id` | UUID | Identificador técnico generado por la base de datos |
| `clave` | String(100) | Obligatoria, única, patrón `E-[A-Z0-9]{1,97}` y normalizada a mayúsculas |
| `nombre` | String(100) | Obligatorio, no vacío |
| `direccion` | String(100) | Obligatoria, no vacía |
| `telefono` | String(100) | Obligatorio, no vacío |
| `password` | String(255) | Obligatoria; siempre persistida codificada (nunca en texto plano) |
| `departamento_id` | UUID nullable | FK opcional hacia `departamentos.id` |
| `createdAt` | OffsetDateTime | Timestamp de creación (auditado) |
| `updatedAt` | OffsetDateTime | Timestamp de actualización (auditado) |

### Restricciones relevantes

- `uq_empleados_clave` garantiza unicidad por `clave`.
- `idx_empleados_clave` optimiza búsqueda por `clave` para autenticación.
- La relación `Empleado -> Departamento` es muchos-a-uno opcional.

## 2) Modelos de entrada

### `EmpleadoCreateRequest` / `EmpleadoUpdateRequest`

| Campo | Tipo | Regla |
|------|------|-------|
| `clave` | String | `@NotBlank` + patrón `E-[A-Z0-9]{1,97}` |
| `nombre` | String | `@NotBlank`, máximo 100 |
| `direccion` | String | `@NotBlank`, máximo 100 |
| `telefono` | String | `@NotBlank`, máximo 100 |
| `password` | String | `@NotBlank`, mínimo 8, máximo 100 |
| `departamentoId` | UUID nullable | Opcional; si viene informado debe existir |

## 3) Modelo de salida

### `EmpleadoResponse`

| Campo | Tipo | Nota |
|------|------|------|
| `id` | UUID | Identificador técnico |
| `clave` | String | Clave normalizada |
| `nombre` | String | Nombre del empleado |
| `direccion` | String | Dirección del empleado |
| `telefono` | String | Teléfono del empleado |
| `departamentoId` | UUID nullable | Departamento asociado (si existe) |
| `createdAt` | OffsetDateTime | Auditoría |
| `updatedAt` | OffsetDateTime | Auditoría |

El campo `password` está excluido del contrato de salida.

## 4) Modelo de paginación

`GET /api/v1/empleados` devuelve `Page<EmpleadoResponse>` con:

- `content`: colección de empleados de la página actual.
- `totalElements`: total de empleados.
- `totalPages`: total de páginas.
- `size`: tamaño efectivo de página.
- `number`: índice de página actual.

### Reglas de paginación

- Valores por defecto: `page=0`, `size=10`, `sort=clave,asc`.
- Si `size > 100`, se recorta a `100`.
- Si `page < 0` o `size <= 0`, se responde `400 Bad Request`.

## 5) Estados y transiciones clave

### Estado de bootstrap

1. **Sin empleados (`count = 0`)**
	- `POST /api/v1/empleados` puede ejecutarse sin autenticación.
2. **Con al menos un empleado (`count > 0`)**
	- `POST /api/v1/empleados` requiere autenticación HTTP Basic.

### Ciclo de vida de `password`

1. **Alta**: contraseña en request -> codificación -> persistencia.
2. **Actualización**: nueva contraseña en request -> codificación -> reemplazo del hash previo.
3. **Autenticación**: comparación de credencial entrante contra hash persistido.