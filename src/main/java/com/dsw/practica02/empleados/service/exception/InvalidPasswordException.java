package com.dsw.practica02.empleados.service.exception;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("La contrasena debe tener entre 8 y 100 caracteres");
    }
}
