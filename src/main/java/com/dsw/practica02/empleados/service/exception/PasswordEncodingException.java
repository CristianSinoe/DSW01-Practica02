package com.dsw.practica02.empleados.service.exception;

public class PasswordEncodingException extends RuntimeException {

    public PasswordEncodingException() {
        super("No fue posible codificar la contraseña del empleado");
    }
}