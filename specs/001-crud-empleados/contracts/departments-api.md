# Departments API Contract

- **Base URL**: `/api/v1/departamentos`
- **Authentication**: HTTP Basic con `username = clave` y `password = password` de un empleado existente.
- **Media Type**: `application/json`
- **Identifiers**: los endpoints puntuales usan `id` tipo UUID.
- **Relationship**: un departamento puede estar asociado a múltiples empleados mediante `empleados.departamento_id`.

## Create department

- **Method/Path**: `POST /api/v1/departamentos`
- **Request Body**:
  ```json
  {
    "nombre": "Recursos Humanos"
  }
  ```
- **Responses**:
  - `201 Created` con el departamento creado.
  - `400 Bad Request` si el nombre viene vacío o supera 100 caracteres.
  - `409 Conflict` si ya existe un departamento con el mismo nombre.

## List departments

- **Method/Path**: `GET /api/v1/departamentos`
- **Query Params**:
  - `page` opcional, default `0`
  - `size` opcional, default `10`
  - `sort` opcional, default `nombre,asc`
- **Response `200 OK`**:
  ```json
  {
    "content": [
      {
        "id": "6af8d3f0-bf87-4b0f-9fd4-2f8af643bb28",
        "nombre": "Recursos Humanos",
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

## Get department by id

- **Method/Path**: `GET /api/v1/departamentos/{id}`
- **Path Variable**: `id` UUID.
- **Responses**:
  - `200 OK` con el objeto `Departamento`.
  - `404 Not Found` si el `id` no existe.

## Update department

- **Method/Path**: `PUT /api/v1/departamentos/{id}`
- **Request Body**:
  ```json
  {
    "nombre": "Recursos Humanos y Cultura"
  }
  ```
- **Responses**:
  - `200 OK` con el recurso actualizado.
  - `400 Bad Request` si falla validación.
  - `404 Not Found` si el `id` no existe.
  - `409 Conflict` si el nombre ya existe.

## Delete department

- **Method/Path**: `DELETE /api/v1/departamentos/{id}`
- **Responses**:
  - `204 No Content` si se elimina correctamente.
  - `404 Not Found` si el `id` no existe.
  - `409 Conflict` si el departamento tiene empleados asignados.
