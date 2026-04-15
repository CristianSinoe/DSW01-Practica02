package com.dsw.practica02.empleados.dto;

import com.dsw.practica02.empleados.domain.EmpleadoRole;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EmpleadoResponse(
        UUID id,
        String nombre,
        String email,
        String telefono,
        String direccion,
        EmpleadoRole role,
        UUID departamentoId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
