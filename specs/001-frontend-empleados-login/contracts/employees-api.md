# Employees API Contract (Email Login + Roles)

- **Base URL**: `/api/v1/empleados`
- **Media Type**: `application/json`
- **Identifiers**: `id` tipo UUID
- **Authentication**: HTTP Basic con `email:password`

## Role Access Matrix

| Endpoint | ADMIN | EMPLEADO |
|---|---|---|
| `GET /api/v1/empleados` | ✅ | ✅ |
| `GET /api/v1/empleados/{id}` | ✅ | ✅ |
| `POST /api/v1/empleados` | ✅ | ❌ (`403`) |
| `PUT /api/v1/empleados/{id}` | ✅ | ❌ (`403`) |
| `DELETE /api/v1/empleados/{id}` | ✅ | ❌ (`403`) |

## List employees

- **Method/Path**: `GET /api/v1/empleados`
- **Query Params**:
  - `page` opcional, default `0`
  - `size` opcional, default `10`
  - `sort` opcional, default `email,asc`
- **Response `200 OK`**:
  ```json
  {
    "content": [
      {
        "id": "a8f9aa51-3ec8-45f7-a8f4-1d354c38d8e0",
        "nombre": "Ana Pérez",
        "email": "ana@empresa.com",
        "telefono": "555-0101",
        "direccion": "Av. Central 123",
        "role": "EMPLEADO",
        "departamentoId": null,
        "createdAt": "2026-03-13T10:00:00Z",
        "updatedAt": "2026-03-13T10:00:00Z"
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
- **Responses**:
  - `200 OK` con recurso encontrado
  - `404 Not Found` si no existe

## Create employee (ADMIN)

- **Method/Path**: `POST /api/v1/empleados`
- **Request Body**:
  ```json
  {
    "nombre": "Ana Pérez",
    "email": "ana@empresa.com",
    "telefono": "555-0101",
    "direccion": "Av. Central 123",
    "password": "AnaSegura123",
    "departamentoId": null,
    "role": "EMPLEADO"
  }
  ```
- **Rules**:
  - `password` obligatoria en alta
  - `email` único
- **Responses**:
  - `201 Created`
  - `400 Bad Request` por validación
  - `403 Forbidden` si role no autorizado
  - `409 Conflict` por email duplicado

## Update employee (ADMIN)

- **Method/Path**: `PUT /api/v1/empleados/{id}`
- **Request Body**:
  ```json
  {
    "nombre": "Ana P.",
    "email": "ana@empresa.com",
    "telefono": "555-0202",
    "direccion": "Av. Central 321",
    "password": "",
    "departamentoId": null,
    "role": "EMPLEADO"
  }
  ```
- **Rules**:
  - `password` opcional: si no se envía valor útil, se conserva la actual
- **Responses**:
  - `200 OK`
  - `400 Bad Request`
  - `403 Forbidden`
  - `404 Not Found`
  - `409 Conflict`

## Delete employee (ADMIN, hard delete)

- **Method/Path**: `DELETE /api/v1/empleados/{id}`
- **Responses**:
  - `204 No Content`
  - `403 Forbidden`
  - `404 Not Found`

## Error Format

```json
{
  "timestamp": "2026-03-13T10:00:00Z",
  "path": "/api/v1/empleados",
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Acceso denegado"
}
```

## Security Notes

- `password` nunca se expone en respuestas.
- `email` es el identificador canónico de login.
- Operaciones de mutación están restringidas a `superusuario/admin`.
