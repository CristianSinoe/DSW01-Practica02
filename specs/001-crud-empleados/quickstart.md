# Quickstart – CRUD de empleados

## Prerrequisitos
- Java 17 y Maven Wrapper (`./mvnw`).
- Docker y Docker Compose 2.x.
- Variables de entorno definidas:
   - `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`.

## Pasos
1. **Levantar PostgreSQL**
   ```bash
   docker compose up db -d
   ```
   Verifica que el contenedor `db` exponga el puerto `5432`.
2. **Aplicar migraciones Flyway**
   ```bash
   ./mvnw -Dflyway.configFiles=flyway.conf flyway:migrate
   ```
   (Alternativamente, el arranque del API ejecutará las migraciones automáticamente si la config está en `application.properties`).
3. **Ejecutar la API**
   ```bash
   ./mvnw spring-boot:run
   ```
   El servicio queda disponible en `http://localhost:8080`.
4. **Bootstrap del primer empleado (sin autenticación)**
   ```bash
   curl -H 'Content-Type: application/json' \
      -d '{"clave":"E-001","nombre":"Ana","direccion":"Centro","telefono":"555","password":"AnaSegura123"}' \
      -X POST http://localhost:8080/api/v1/empleados
   ```
   Debe responder `201 Created`.
5. **Probar autenticación basada en Empleado**
   ```bash
   curl -u "E-001:AnaSegura123" "http://localhost:8080/api/v1/empleados?page=0&size=10&sort=clave,asc"
   ```
   Debe responder `200 OK` con respuesta paginada (`content`, `totalElements`, `totalPages`, `size`, `number`).
6. **Probar flujo CRUD completo de empleados**
   ```bash
   curl -u "E-001:AnaSegura123" \
      -H 'Content-Type: application/json' \
      -d '{"clave":"E-002","nombre":"Luis","direccion":"Norte","telefono":"555-0202","password":"LuisClave123"}' \
      -X POST http://localhost:8080/api/v1/empleados
   ```
   Luego consulta por ID, actualiza y elimina para completar el ciclo.

## Verificaciones adicionales
- Abre `http://localhost:8080/swagger-ui.html` para revisar la documentación expuesta por Springdoc.
- Ejecuta pruebas automáticas:
  ```bash
  ./mvnw test
  ```
  Las pruebas de integración arrancarán un contenedor PostgreSQL mediante Testcontainers.

## Notas funcionales
- La entidad `Empleado` incluye el campo `password` y este no se expone en respuestas de la API.
- El listado de empleados es paginado (`Page`) y no retorna todos los registros en una única consulta.
- La autenticación HTTP Basic valida usuario/contraseña contra registros persistidos de `Empleado`.
