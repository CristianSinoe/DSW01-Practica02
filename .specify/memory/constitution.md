<!--
Sync Impact Report
- Version change: 1.1.0 → 2.0.0
- Modified principles:
	- I. Stack Tecnológico Innegociable → I. Stack Tecnológico Innegociable (actualiza requisito de frontend a Angular 20 LTS)
	- VI. Frontend Web Estandarizado en Angular 19 → VI. Frontend Web Estandarizado en Angular 20 LTS
- Added sections:
	- Ninguna
- Removed sections:
	- Ninguna
- Templates requiring updates:
	- ✅ .specify/templates/plan-template.md
	- ✅ .specify/templates/spec-template.md
	- ✅ .specify/templates/tasks-template.md
	- ⚠ pending: .specify/templates/commands/*.md (directorio inexistente en este repositorio)
- Runtime guidance reviewed:
	- ✅ specs/001-crud-empleados/quickstart.md (sin cambios)
	- ✅ specs/002-crud-departamentos/quickstart.md (sin cambios)
	- ✅ specs/003-password/quickstart.md (sin cambios)
	- ✅ .github/agents/speckit.constitution.agent.md (sin cambios)
- Follow-up TODOs:
	- Ninguno
-->

# Constitución del Proyecto Full Stack

## Principios Fundamentales

### I. Stack Tecnológico Innegociable
El backend DEBE desarrollarse exclusivamente con Spring Boot 3 y Java 17.
Cuando una funcionalidad incluya interfaz web, el frontend DEBE implementarse con Angular 20 LTS.
Toda nueva funcionalidad DEBE seguir convenciones del ecosistema elegido (Spring en backend,
Angular 20 LTS en frontend), con separación clara de capas y configuración por entorno.
No se aceptan librerías o patrones que contradigan este stack sin una ADR aprobada
por el equipo técnico.
Rationale: un stack homogéneo reduce riesgo operativo, acelera revisiones y simplifica
mantenimiento.

### II. Seguridad por Defecto
La API DEBE estar protegida con autenticación básica (HTTP Basic) desde el inicio,
salvo que una especificación aprobada exija un mecanismo más robusto.
Ningún endpoint sensible PUEDE exponerse sin control de acceso.
Las credenciales, secretos y configuraciones de seguridad DEBEN manejarse por
variables de entorno o perfiles, nunca hardcodeadas.
Rationale: la seguridad temprana evita deuda crítica y reduce exposición accidental.

### III. Persistencia Consistente con PostgreSQL
La base de datos oficial del proyecto es PostgreSQL.
Todos los cambios de modelo DEBEN reflejarse de forma controlada mediante
migraciones versionadas (por ejemplo, Flyway o Liquibase).
Se prohíbe depender de motores embebidos en producción; las pruebas DEBEN simular
el comportamiento real de PostgreSQL.
Rationale: la consistencia del esquema y su trazabilidad evitan desviaciones entre
entornos.

### IV. Entorno Reproducible con Docker
El proyecto DEBE poder levantarse en local con Docker y Docker Compose para
garantizar paridad de entornos.
DEBE existir como mínimo un servicio para la API y otro para PostgreSQL, con red,
volúmenes y variables documentadas.
Si hay frontend web, DEBE integrarse al flujo reproducible de ejecución o build
documentado.
Cualquier miembro del equipo DEBE poder ejecutar el sistema con un único flujo
estándar.
Rationale: la reproducibilidad disminuye fallos por diferencias de entorno.

### V. API Contratada y Documentada
La API REST DEBE mantenerse documentada con Swagger/OpenAPI de manera actualizada.
Cada endpoint nuevo o modificado REQUIERE contrato, ejemplos de request/response y
códigos de error esperados.
No se considera completa una historia sin documentación de API verificable.
Rationale: contratos explícitos reducen ambigüedad e integraciones defectuosas.

### VI. Frontend Web Estandarizado en Angular 20 LTS
Toda interfaz web DEBE implementarse con Angular 20 LTS y organizarse por dominios
(`core`, `shared`, `features`) para mantener modularidad.
El frontend DEBE consumir contratos versionados de la API y centralizar manejo de
autenticación y errores mediante servicios/interceptores.
La configuración por entorno DEBE resolverse sin hardcodear endpoints o secretos.
Rationale: una base frontend única mejora coherencia UX, mantenibilidad y velocidad
de entrega.

## Restricciones Técnicas y de Calidad

- Arquitectura base por capas: `controller`, `service`, `repository`, `domain`/`dto`.
- Arquitectura frontend Angular 20 LTS por dominios: `core`, `shared`, `features`.
- Validación obligatoria de entrada en todos los endpoints públicos y formularios web.
- Manejo centralizado de errores con respuestas consistentes en backend y frontend.
- Configuración por perfiles (`dev`, `test`, `prod`) y `environment` por entorno en UI.
- Logs estructurados suficientes para diagnóstico, sin exponer datos sensibles.
- Convención de versionado de API y compatibilidad explícita ante cambios breaking.

## Flujo de Desarrollo y Puertas de Calidad

- Cada cambio funcional DEBE incluir pruebas automáticas (unitarias y/o integración,
	según alcance de backend/frontend).
- Todo cambio de seguridad, base de datos, contrato API o UI Angular REQUIERE revisión
	técnica obligatoria.
- Antes de integrar, el proyecto DEBE compilar, pasar pruebas y levantar correctamente
	con Docker; si hay frontend, su build DEBE ser exitoso.
- La documentación Swagger/OpenAPI DEBE validarse en cada feature relevante.
- No se aprueban PRs con deuda crítica de seguridad, ausencia de pruebas o
	configuraciones no reproducibles.

## Gobernanza

Esta constitución prevalece sobre prácticas ad hoc del equipo.

Procedimiento de enmienda:
1. Proponer cambio con impacto explícito en principios, plantillas y guías de ejecución.
2. Obtener aprobación del equipo técnico responsable.
3. Actualizar versión y fecha de enmienda en este documento.
4. Registrar excepciones vigentes con alcance, riesgos y fecha de revisión.

Política de versionado de la constitución (SemVer):
- MAJOR: eliminación o redefinición incompatible de principios/gobernanza.
- MINOR: adición de principios o expansión material de reglas obligatorias.
- PATCH: aclaraciones editoriales sin cambio de obligación normativa.

Revisión de cumplimiento:
- Todo `plan.md` DEBE ejecutar un Constitution Check antes de diseño detallado.
- Todo `spec.md` DEBE reflejar requisitos de seguridad, contratos y stack aplicable.
- Todo `tasks.md` DEBE incluir tareas de verificación para principios afectados.
- Toda PR DEBE evidenciar cumplimiento de los principios aplicables o justificar
	excepción documentada.

**Version**: 2.0.0 | **Ratified**: 2026-02-25 | **Last Amended**: 2026-03-11
