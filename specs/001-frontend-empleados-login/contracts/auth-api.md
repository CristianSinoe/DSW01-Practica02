# Auth API Contract

- **Base URL**: `/api/v1/auth`
- **Media Type**: `application/json`
- **Auth Scheme**: HTTP Basic con credenciales `email:password`

## Endpoint: obtener sesión autenticada

- **Method/Path**: `GET /api/v1/auth/me`
- **Headers**:
  - `Authorization: Basic <base64(email:password)>`
- **Behavior**:
  - Valida credenciales contra `Empleado.email` + `Empleado.password` codificado.
  - Retorna identidad y rol para inicializar estado del frontend.
- **Response `200 OK`**:
  ```json
  {
    "id": "a8f9aa51-3ec8-45f7-a8f4-1d354c38d8e0",
    "nombre": "Admin Inicial",
    "email": "admin@empresa.com",
    "role": "ADMIN"
  }
  ```
- **Error Responses**:
  - `401 Unauthorized` cuando las credenciales son inválidas.

## Logout

- No requiere endpoint backend dedicado para este alcance.
- El frontend realiza logout limpiando estado de sesión local y removiendo credenciales almacenadas.

## Bootstrap de admin inicial

- El backend crea automáticamente un único `superusuario/admin` en arranque si no existe ninguno.
- El bootstrap usa variables de entorno (`ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ADMIN_NOMBRE`, `ADMIN_TELEFONO`, `ADMIN_DIRECCION`).
- Si ya existe un admin, no se crean duplicados (idempotente).

## Error Format

```json
{
  "timestamp": "2026-03-13T10:00:00Z",
  "path": "/api/v1/auth/me",
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "Credenciales inválidas"
}
```
