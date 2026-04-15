package com.dsw.practica02.empleados.config;

import com.dsw.practica02.empleados.service.exception.ClaveDuplicadaException;
import com.dsw.practica02.empleados.service.exception.EmailDuplicadaException;
import com.dsw.practica02.empleados.service.exception.DepartamentoDuplicadoException;
import com.dsw.practica02.empleados.service.exception.DepartamentoEnUsoException;
import com.dsw.practica02.empleados.service.exception.DepartamentoNotFoundException;
import com.dsw.practica02.empleados.service.exception.EmpleadoNotFoundException;
import com.dsw.practica02.empleados.service.exception.InvalidPaginationException;
import com.dsw.practica02.empleados.service.exception.InvalidPasswordException;
import com.dsw.practica02.empleados.service.exception.PasswordEncodingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::formatFieldError)
                .findFirst()
                .orElse("Petición inválida");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Petición inválida");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(InvalidPaginationException.class)
    ResponseEntity<ApiError> handleInvalidPagination(InvalidPaginationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler({EmpleadoNotFoundException.class, DepartamentoNotFoundException.class})
    ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler({
            ClaveDuplicadaException.class,
            EmailDuplicadaException.class,
            DepartamentoDuplicadoException.class,
            DepartamentoEnUsoException.class,
            DataIntegrityViolationException.class
    })
    ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(PasswordEncodingException.class)
    ResponseEntity<ApiError> handlePasswordEncoding(PasswordEncodingException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    ResponseEntity<ApiError> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    ResponseEntity<ApiError> handleAuthenticationRequired(
            AuthenticationCredentialsNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Autenticación requerida", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado", request);
    }

    private static String formatFieldError(FieldError error) {
        return "%s: %s".formatted(error.getField(), error.getDefaultMessage());
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiError payload = ApiError.of(status, message, request);
        return ResponseEntity.status(status).body(payload);
    }
}
