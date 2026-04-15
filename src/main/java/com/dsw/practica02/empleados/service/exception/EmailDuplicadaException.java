package com.dsw.practica02.empleados.service.exception;

public class EmailDuplicadaException extends RuntimeException {

    public EmailDuplicadaException(String email) {
        super("Ya existe un empleado con email %s".formatted(email));
    }
}
