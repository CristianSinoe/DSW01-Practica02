package com.dsw.practica02.empleados.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DepartamentoResponse(
        UUID id,
        String nombre,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
