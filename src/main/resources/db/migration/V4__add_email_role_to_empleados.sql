ALTER TABLE empleados
    ADD COLUMN IF NOT EXISTS email VARCHAR(150),
    ADD COLUMN IF NOT EXISTS role VARCHAR(20);

UPDATE empleados
SET email = lower(clave) || '@empresa.local'
WHERE email IS NULL OR btrim(email) = '';

UPDATE empleados
SET role = 'EMPLEADO'
WHERE role IS NULL OR btrim(role) = '';

ALTER TABLE empleados
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN role SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_empleados_email_lower ON empleados (lower(email));

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_empleados_role'
    ) THEN
        ALTER TABLE empleados
            ADD CONSTRAINT chk_empleados_role CHECK (role IN ('ADMIN', 'EMPLEADO'));
    END IF;
END;
$$;
