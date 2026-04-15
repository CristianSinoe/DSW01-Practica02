# Quickstart: Frontend CRUD de Empleados con Login y Roles

## Prerrequisitos

- Docker y Docker Compose 2.x
- Java 17 y Maven Wrapper (`./mvnw`) para ejecución local backend
- Node.js LTS (recomendado 20+) para ejecución local frontend

## 1) Configurar variables de entorno

Definir variables mínimas para bootstrap y servicios:

```bash
POSTGRES_DB=empleados
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
DB_PORT=5432
SERVER_PORT=8080
FRONTEND_PORT=4200

ADMIN_EMAIL=admin@empresa.com
ADMIN_PASSWORD=AdminSegura123
ADMIN_NOMBRE=Admin Inicial
ADMIN_TELEFONO=555-0000
ADMIN_DIRECCION=Oficina Central
```

## 2) Levantar stack con Docker Compose

```bash
docker compose up --build -d
```

Resultado esperado:

- `db` en estado healthy
- `api` levantada en `http://localhost:8080`
- `frontend` levantado en `http://localhost:4200`

## 3) Verificar bootstrap de `superusuario/admin`

```bash
curl -i -H "Authorization: Basic $(printf 'admin@empresa.com:AdminSegura123' | base64)" \
  http://localhost:8080/api/v1/auth/me
```

Resultado esperado: `200 OK` con `role: "ADMIN"`.

## 4) Probar flujo admin (CRUD completo)

1. Ingresar a `http://localhost:4200`.
2. Login con `admin@empresa.com`.
3. Crear un empleado con `nombre`, `email`, `telefono`, `direccion`, `password`.
4. Editar el empleado sin enviar password (debe conservarse).
5. Eliminar el empleado (hard delete).

## 5) Probar flujo empleado (solo lectura)

1. Login con una cuenta de rol `EMPLEADO`.
2. Confirmar que puede listar/ver detalle.
3. Confirmar que no puede crear/editar/eliminar (respuesta `403` o controles UI deshabilitados).

## 6) Ejecución local sin Docker (opcional)

### Backend

```bash
docker compose up db -d
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm start
```

## 7) Validación rápida de calidad

```bash
./mvnw test
cd frontend && npm test
```

## Notas

- Los contratos de integración están en `contracts/auth-api.md` y `contracts/employees-api.md`.
- La documentación OpenAPI debe quedar alineada con estos contratos antes de cerrar la implementación.
