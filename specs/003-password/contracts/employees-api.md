# Employees API Contract (Password, Auth & Pagination)

- **Base URL**: `/api/v1/empleados`
- **Media Type**: `application/json`
- **Identifiers**: endpoints puntuales usan `id` UUID.
- **Error Format** (errores de aplicación):
  ```json
  {
    "timestamp": "2026-03-10T10:00:00Z",
    "path": "/api/v1/empleados",
    "status": 400,
    "error": "BAD_REQUEST",
    "message": "size debe ser mayor que 0"
  }
  ```

## Authentication & Authorization

- Esquema: HTTP Basic.
- Usuario: `clave` del empleado.
- Contraseña: `password` del empleado (validada contra hash persistido).
- Modelo de roles para este feature: todo empleado autenticado opera como `ADMIN`.

### Regla de bootstrap

- `POST /api/v1/empleados` es público **solo** cuando el total de empleados es `0`.
- Después de crear el primer empleado, `POST /api/v1/empleados` requiere autenticación.

## Create employee

- **Method/Path**: `POST /api/v1/empleados`
- **Request Body**:
  ```json
  {
    "clave": "E-001",
    "nombre": "Ana Perez",
    "direccion": "Av. Central 123",
    "telefono": "555-0101",
    "password": "AnaSegura123",
    "departamentoId": "6af8d3f0-bf87-4b0f-9fd4-2f8af643bb28"
  }
  ```
- `departamentoId` es opcional.
- **Responses**:
  - `201 Created` cuando la alta es exitosa.
  - `400 Bad Request` por validación (incluye `password` obligatoria de 8-100 caracteres).
  - `401 Unauthorized` cuando ya existe al menos un empleado y no se envían credenciales.
  - `404 Not Found` si `departamentoId` no existe.
  - `409 Conflict` si la `clave` ya está registrada.

## List employees (paginated)

- **Method/Path**: `GET /api/v1/empleados`
- **Auth**: requerida.
- **Query Params**:
  - `page`: opcional, default `0`, debe ser `>= 0`.
  - `size`: opcional, default `10`, debe ser `> 0`.
  - `sort`: opcional, default `clave,asc`.
- **Behavior**:
  - Si `size > 100`, el servicio recorta el valor a `100`.
  - Si `page < 0` o `size <= 0`, responde `400 Bad Request`.
  - La respuesta es un `Page` con metadatos de paginación.
- **Response `200 OK` (ejemplo)**:
  ```json
  {
    "content": [
      {
        "id": "aea829ff-da21-400a-af80-15b35b6e82ba",
        "clave": "E-001",
        "nombre": "Ana Perez",
        "direccion": "Av. Central 123",
        "telefono": "555-0101",
        "departamentoId": "6af8d3f0-bf87-4b0f-9fd4-2f8af643bb28",
        "createdAt": "2026-03-10T10:00:00Z",
        "updatedAt": "2026-03-10T10:00:00Z"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
  ```

## Get employee by id

- **Method/Path**: `GET /api/v1/empleados/{id}`
- **Auth**: requerida.
- **Responses**:
  - `200 OK` con empleado.
  - `404 Not Found` si no existe.

## Update employee

- **Method/Path**: `PUT /api/v1/empleados/{id}`
- **Auth**: requerida.
- **Request Body**: mismo contrato de alta, con `password` obligatoria.
- **Responses**:
  - `200 OK` con recurso actualizado.
  - `400 Bad Request` por validación.
  - `404 Not Found` si empleado/departamento no existen.
  - `409 Conflict` por `clave` duplicada.

## Delete employee

- **Method/Path**: `DELETE /api/v1/empleados/{id}`
- **Auth**: requerida.
- **Responses**:
  - `204 No Content` al eliminar.
  - `404 Not Found` si no existe.

## Security Notes

- `password` nunca se expone en contratos de salida.
- `password` se persiste codificada; no se almacena texto plano.
- Credenciales inválidas en endpoints protegidos deben resultar en rechazo de autenticación.