package com.dsw.practica02.empleados.service.exception;

import java.util.UUID;

public class DepartamentoEnUsoException extends RuntimeException {

    public DepartamentoEnUsoException(UUID id) {
        super("No se puede eliminar el departamento %s porque tiene empleados asignados".formatted(id));
    }
}
