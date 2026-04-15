package com.dsw.practica02.empleados.config;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;

public record ApiError(
        OffsetDateTime timestamp,
        String path,
        int status,
        String error,
        String message
) {

        public static ApiError of(HttpStatus status, String message, HttpServletRequest request) {
                return new ApiError(
                                OffsetDateTime.now(),
                                request.getRequestURI(),
                                status.value(),
                                status.name(),
                                message
                );
        }

        public static ApiError of(int status, String error, String message, String path) {
                return new ApiError(
                                OffsetDateTime.now(),
                                path,
                                status,
                                error,
                                message
                );
        }
}
