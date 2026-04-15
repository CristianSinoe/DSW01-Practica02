# Employees API Contract

- **Base URL**: `/api/v1/empleados`
- **Authentication**:
  - `POST /api/v1/empleados` es público para bootstrap del primer usuario.
  - `GET`, `PUT` y `DELETE` requieren HTTP Basic con `username = clave` y `password` del empleado autenticado.
  - La autenticación valida credenciales contra la entidad `Empleado` (lookup por `clave` y verificación de `password` almacenada de forma codificada).
- **Media Type**: `application/json`
- **Identifiers**: los endpoints puntuales usan `id` tipo UUID.
- **Error Format**:
  ```json
  {
    "timestamp": "2026-03-10T10:00:00Z",
    "path": "/api/v1/empleados/aea829ff-da21-400a-af80-15b35b6e82ba",
    "status": 404,
    "error": "NOT_FOUND",
    "message": "Empleado con id aea829ff-da21-400a-af80-15b35b6e82ba no encontrado"
  }
  ```

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
- `departamentoId` es opcional. Si se omite o es `null`, el empleado queda sin departamento.
- **Responses**:
  - `201 Created` con el empleado creado.
  - `400 Bad Request` si falla validación (incluye `password` obligatoria de 8-100 caracteres).
  - `404 Not Found` si `departamentoId` no existe.
  - `409 Conflict` si `clave` ya existe.

## List employees

- **Method/Path**: `GET /api/v1/empleados`
- **Query Params**:
  - `page` opcional, default `0`
  - `size` opcional, default `10`
  - `sort` opcional, default `clave,asc`
- El endpoint devuelve un objeto paginado (`Page`) y no la colección completa en una sola consulta.
- **Response `200 OK`**:
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
- **Path Variable**: `id` UUID.
- **Responses**:
  - `200 OK` con el objeto `Empleado`.
  - `404 Not Found` si el `id` no existe.

## Update employee

- **Method/Path**: `PUT /api/v1/empleados/{id}`
- **Request Body**:
  ```json
  {
    "clave": "E-001",
    "nombre": "Ana P.",
    "direccion": "Av. Central 321",
    "telefono": "555-0202",
    "password": "AnaNuevaClave123",
    "departamentoId": "6af8d3f0-bf87-4b0f-9fd4-2f8af643bb28"
  }
  ```
- `departamentoId` puede ser `null` para desasignar al empleado de un departamento.
- **Responses**:
  - `200 OK` con el recurso actualizado.
  - `400 Bad Request` si falla validación (incluye `password` obligatoria de 8-100 caracteres).
  - `404 Not Found` si el empleado o el departamento no existen.
  - `409 Conflict` si la `clave` entra en conflicto con otro empleado.

## Delete employee

- **Method/Path**: `DELETE /api/v1/empleados/{id}`
- **Responses**:
  - `204 No Content` al eliminar físicamente el registro.
  - `404 Not Found` si el `id` no existe.
