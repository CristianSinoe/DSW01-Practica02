# Quickstart: Ajustes de Password, Auth y Paginación de Empleados

## Prerrequisitos

- Java 17 y Maven Wrapper (`./mvnw`).
- Docker y Docker Compose 2.x.
- Puerto `8080` libre para la API.

## 1) Levantar base de datos

```bash
docker compose up db -d
```

## 2) Ejecutar API

```bash
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080`.

## 3) Bootstrap del primer empleado (público)

```bash
curl -i -H 'Content-Type: application/json' \
   -d '{"clave":"E-001","nombre":"Ana","direccion":"Centro","telefono":"555","password":"AnaSegura123"}' \
   -X POST http://localhost:8080/api/v1/empleados
```

Resultado esperado: `201 Created`.

## 4) Verificar que nuevas altas ya requieren autenticación

```bash
curl -i -H 'Content-Type: application/json' \
   -d '{"clave":"E-002","nombre":"Luis","direccion":"Norte","telefono":"556","password":"LuisClave123"}' \
   -X POST http://localhost:8080/api/v1/empleados
```

Resultado esperado: `401 Unauthorized` (después del bootstrap inicial).

## 5) Alta autenticada con empleado existente

```bash
curl -i -u "E-001:AnaSegura123" -H 'Content-Type: application/json' \
   -d '{"clave":"E-002","nombre":"Luis","direccion":"Norte","telefono":"556","password":"LuisClave123"}' \
   -X POST http://localhost:8080/api/v1/empleados
```

Resultado esperado: `201 Created`.

## 6) Verificar listado paginado

Consulta por defecto:

```bash
curl -i -u "E-001:AnaSegura123" "http://localhost:8080/api/v1/empleados"
```

Resultado esperado: `200 OK` con `content`, `totalElements`, `totalPages`, `size`, `number`.

Consulta con `size` mayor al límite:

```bash
curl -i -u "E-001:AnaSegura123" "http://localhost:8080/api/v1/empleados?page=0&size=500"
```

Resultado esperado: `200 OK` con tamaño efectivo recortado a `100`.

Consulta inválida:

```bash
curl -i -u "E-001:AnaSegura123" "http://localhost:8080/api/v1/empleados?page=-1&size=10"
```

Resultado esperado: `400 Bad Request`.

## 7) Verificar que `password` no se expone

- Revisar respuestas de `GET /api/v1/empleados` y `GET /api/v1/empleados/{id}`.
- Confirmar que los objetos de salida no contienen el campo `password`.