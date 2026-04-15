CREATE TABLE IF NOT EXISTS departamentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_departamentos_nombre_upper ON departamentos (upper(nombre));

DROP TRIGGER IF EXISTS tr_departamentos_set_updated_at ON departamentos;
CREATE TRIGGER tr_departamentos_set_updated_at
    BEFORE UPDATE ON departamentos
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

ALTER TABLE empleados
    ADD COLUMN IF NOT EXISTS departamento_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_empleados_departamento'
    ) THEN
        ALTER TABLE empleados
            ADD CONSTRAINT fk_empleados_departamento
                FOREIGN KEY (departamento_id) REFERENCES departamentos(id);
    END IF;
END;
$$;

CREATE INDEX IF NOT EXISTS idx_empleados_departamento_id ON empleados (departamento_id);
