package com.dsw.practica02.empleados.dto;

import com.dsw.practica02.empleados.domain.Departamento;
import java.util.Objects;

public final class DepartamentoMapper {

    private DepartamentoMapper() {
        // Utility
    }

    public static DepartamentoResponse toResponse(Departamento departamento) {
        Objects.requireNonNull(departamento, "departamento");
        return new DepartamentoResponse(
                departamento.getId(),
                departamento.getNombre(),
                departamento.getCreatedAt(),
                departamento.getUpdatedAt()
        );
    }
}
