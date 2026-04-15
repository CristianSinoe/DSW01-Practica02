package com.dsw.practica02.empleados.service.exception;

import java.util.UUID;

public class DepartamentoNotFoundException extends RuntimeException {

    public DepartamentoNotFoundException(UUID id) {
        super("Departamento con id %s no encontrado".formatted(id));
    }
}
