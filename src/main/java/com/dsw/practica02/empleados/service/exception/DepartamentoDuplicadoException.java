package com.dsw.practica02.empleados.service.exception;

public class DepartamentoDuplicadoException extends RuntimeException {

    public DepartamentoDuplicadoException(String nombre) {
        super("Departamento con nombre %s ya existe".formatted(nombre));
    }
}
