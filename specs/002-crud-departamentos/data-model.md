# Data Model: CRUD de Departamentos

**Generated**: 10 de marzo de 2026  
**Phase 1 Output**: Modelo de datos y relaciones para gestión de departamentos  
**Status**: DOCUMENTED - Based on existing implementation

## Core Entities

### Departamento
Representa una división organizacional empresarial dentro del sistema de recursos humanos.

**Attributes:**
- `id` (UUID): Identificador único generado automáticamente, clave primaria
- `nombre` (String, 100 chars max): Nombre del departamento, único en el sistema
- `createdAt` (OffsetDateTime): Timestamp de creación, automático e inmutable
- `updatedAt` (OffsetDateTime): Timestamp de última modificación, automático

**Business Rules:**
- Nombre debe ser único a nivel sistema
- Nombre no puede ser null ni cadena vacía
- Longitud máxima de nombre: 100 caracteres
- IDs son UUID v4 generados automáticamente
- Timestamps se manejan automáticamente por JPA lifecycle hooks

**Validation Rules:**
- `@NotNull` en nombre
- `@Size(max = 100)` en nombre  
- `@Column(unique = true)` constraint en nombre
- UUID formato válido para consultas por ID

### Empleado (Existing Entity - Relationship Context)
Entidad existente que establece la relación with Departamento.

**Relevant Attributes:**
- `id` (UUID): Clave primaria
- `departamento_id` (UUID, nullable): Foreign key hacia Departamento
- `clave` (String): Identificador único de empleado
- `nombre`, `direccion`, `telefono`, `password`: Otros atributos

**Relationship to Departamento:**
- `@ManyToOne(fetch = FetchType.LAZY)` hacia Departamento
- Relación opcional: empleado puede existir sin departamento asignado
- Foreign key constraint: `fk_empleados_departamento`

## Entity Relationships

### Departamento ↔ Empleado
- **Cardinality**: One Departamento to Many Empleados (1:N)
- **Direction**: Unidirectional from Empleado to Departamento  
- **Ownership**: Empleado owns the relationship via FK
- **Cascade**: No cascade operations (independence maintained)
- **Referential Integrity**: 
  - Empleados can exist without Departamento (departamento_id = null)
  - Departamento cannot be deleted if Empleados reference it
  - Orphan Empleados allowed when Departamento is deleted (manual cleanup required)

```text
┌─────────────────┐         ┌─────────────────┐
│   Departamento  │         │    Empleado     │
├─────────────────┤    1:N  ├─────────────────┤
│ id (UUID) PK    │◄────────│ id (UUID) PK    │
│ nombre (unique) │         │ departamento_id │
│ createdAt       │         │ clave (unique)  │
│ updatedAt       │         │ nombre          │
└─────────────────┘         │ direccion       │
                            │ telefono        │
                            │ password        │
                            └─────────────────┘
```

## State Transitions

### Departamento Lifecycle
```text
[CREATION] ──► [ACTIVE] ──► [PENDING_DELETE] ──► [DELETED]
     │             │              │
     │             │              └──► [ACTIVE] (if delete fails)
     │             └──► [UPDATE] ──► [ACTIVE]
     └──► [VALIDATION_ERROR] (rollback)
```

**State Rules:**
- **CREATION**: Name validation, uniqueness check
- **ACTIVE**: Normal operations (read, update) available  
- **PENDING_DELETE**: Check for employee references
- **DELETED**: Permanent removal from system
- **UPDATE**: Name validation and uniqueness check (excluding self)

### Employee Assignment Lifecycle
```text
[UNASSIGNED] ──► [ASSIGNED_TO_DEPT] ──► [REASSIGNED] ──► [UNASSIGNED]
      ▲                │                    │               │
      └────────────────┴────────────────────┴───────────────┘
```

**Assignment Rules:**
- Employees can be unassigned (departamento_id = null)
- Assignment via employee update operation (not department operation)
- Department deletion triggers employee reassignment to null
- No automatic cascading of employee deletions

## Data Constraints & Indexes

### Database Constraints
```sql
-- Primary constraints  
ALTER TABLE departamentos ADD CONSTRAINT pk_departamentos PRIMARY KEY (id);
ALTER TABLE departamentos ADD CONSTRAINT uq_departamentos_nombre UNIQUE (nombre);

-- Foreign key constraints
ALTER TABLE empleados ADD CONSTRAINT fk_empleados_departamento 
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id);
```

### Performance Indexes
```sql
-- Search optimization
CREATE INDEX idx_departamentos_nombre ON departamentos(nombre);
CREATE INDEX idx_empleados_departamento ON empleados(departamento_id);
```

### Validation Summary

| Field | Type | Constraints | Validation |
|-------|------|-------------|------------|
| `id` | UUID | PK, NOT NULL, AUTO | JPA Generated |
| `nombre` | VARCHAR(100) | UNIQUE, NOT NULL | @NotNull, @Size(max=100) |
| `createdAt` | TIMESTAMP | NOT NULL, IMMUTABLE | @PrePersist |
| `updatedAt` | TIMESTAMP | NOT NULL | @PreUpdate |

## Integration Points

### External Systems
- **Security**: Integration with employee authentication for admin verification
- **Audit**: Automatic timestamp tracking for compliance
- **API Gateway**: REST endpoints exposed through Spring Boot

### Data Migration Strategy
- Flyway migrations handle schema evolution
- Existing data preserved during updates
- Foreign key constraints ensure referential integrity
- Migration rollback supported via Flyway