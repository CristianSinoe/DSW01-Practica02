package com.dsw.practica02.empleados.dto;

import com.dsw.practica02.empleados.domain.Empleado;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EmpleadoMapper {

    private EmpleadoMapper() {
        // Utility
    }

    public static EmpleadoResponse toResponse(Empleado empleado) {
        Objects.requireNonNull(empleado, "empleado");
        return new EmpleadoResponse(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getEmail(),
                empleado.getTelefono(),
                empleado.getDireccion(),
                empleado.getRole(),
                empleado.getDepartamento() != null ? empleado.getDepartamento().getId() : null,
                empleado.getCreatedAt(),
                empleado.getUpdatedAt()
        );
    }

    public static List<EmpleadoResponse> toResponse(List<Empleado> empleados) {
        return empleados.stream()
                .map(EmpleadoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
