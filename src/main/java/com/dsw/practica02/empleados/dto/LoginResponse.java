package com.dsw.practica02.empleados.dto;

import com.dsw.practica02.empleados.domain.EmpleadoRole;
import java.util.UUID;

public record LoginResponse(
        UUID id,
        String nombre,
        String email,
        EmpleadoRole role
) {
}
