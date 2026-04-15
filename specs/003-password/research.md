# Research: Ajustes Password Empleado

## Decision 1: Persistir contraseñas usando `PasswordEncoder` delegante de Spring Security

- **Decision**: Mantener `PasswordEncoderFactories.createDelegatingPasswordEncoder()` para codificar `password` en alta y actualización, y validar durante autenticación.
- **Rationale**: Evita texto plano, mantiene compatibilidad con prefijos de algoritmo (`{bcrypt}`, etc.) y sigue convenciones de Spring Boot 3; además permite definir fail-fast cuando la codificación falla.
- **Alternatives considered**:
  - Guardar contraseña sin codificar: descartado por riesgo crítico de seguridad y por incumplir FR-001.
  - Continuar alta/actualización cuando falla codificación: descartado por incumplir FR-015.
  - Forzar un único encoder hardcodeado sin delegación: descartado por menor flexibilidad para migración de algoritmos.

## Decision 2: Autenticar contra `EmpleadoRepository` usando `clave` normalizada

- **Decision**: Resolver usuario de seguridad desde `EmpleadoRepository.findByClaveIgnoreCase(...)`, normalizando `clave` a mayúsculas y asignando rol `ADMIN` para este alcance.
- **Rationale**: Reutiliza la entidad de negocio como fuente de verdad de credenciales, cumple FR-004/FR-012 y evita duplicar almacenamiento de usuarios.
- **Alternatives considered**:
  - Usuarios en memoria: descartado porque no representa datos reales persistidos.
  - Tabla de usuarios separada para este feature: descartado por sobrealcance y mayor costo de migración.

## Decision 3: Política de bootstrap en `POST /api/v1/empleados`

- **Decision**: Permitir alta pública solo mientras el total de empleados sea 0; una vez creado el primero, exigir autenticación para nuevas altas.
- **Rationale**: Cumple la clarificación acordada y evita bloquear la inicialización del sistema sin credenciales previas.
- **Alternatives considered**:
  - Endpoint siempre público: descartado por ampliar superficie de ataque.
  - Endpoint siempre autenticado: descartado porque impide bootstrap inicial.

## Decision 4: Contrato de paginación con defaults y orden estable

- **Decision**: Establecer `page=0`, `size=10` y `sort=clave,asc` por defecto en listado de empleados, devolviendo estructura `Page` con metadatos.
- **Rationale**: Ofrece comportamiento determinista, evita respuestas masivas y facilita consumo del cliente.
- **Alternatives considered**:
  - Devolver lista completa sin paginar: descartado por impacto en rendimiento y usabilidad.
  - Orden implícito por base de datos: descartado por no ser determinista.

## Decision 5: Manejo explícito de límites de paginación

- **Decision**: Recortar `size` solicitado a máximo 100 y responder `400 Bad Request` cuando `page < 0` o `size <= 0`.
- **Rationale**: Cumple las decisiones de clarificación, protege capacidad del servicio y mantiene semántica predecible para errores de cliente.
- **Alternatives considered**:
  - Rechazar también `size > 100` con `400`: descartado por no coincidir con regla acordada de clamping.
  - Ignorar parámetros inválidos y usar defaults silenciosamente: descartado por ocultar errores de cliente.

## Decision 6: Mantener separación explícita entre modelo persistido y respuesta API

- **Decision**: Mantener `EmpleadoResponse` sin campo `password`; mapear entidad a DTO de salida con exclusión de datos sensibles.
- **Rationale**: Reduce riesgo de exposición accidental, refuerza cumplimiento de FR-003 y simplifica revisión de contratos.
- **Alternatives considered**:
  - Serializar entidad JPA directamente: descartado por acoplamiento y riesgo de fuga de campos sensibles.
  - Incluir `password` ofuscada en respuesta: descartado por no aportar valor funcional y aumentar riesgo.
