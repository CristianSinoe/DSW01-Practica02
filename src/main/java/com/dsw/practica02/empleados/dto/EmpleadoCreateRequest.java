package com.dsw.practica02.empleados.dto;

import com.dsw.practica02.empleados.domain.EmpleadoRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record EmpleadoCreateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no debe superar 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser valido")
        @Size(max = 150, message = "El email no debe superar 150 caracteres")
        String email,

        @NotBlank(message = "El telefono es obligatorio")
        @Size(max = 100, message = "El telefono no debe superar 100 caracteres")
        String telefono,

        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 100, message = "La direccion no debe superar 100 caracteres")
        String direccion,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 100, message = "La contrasena debe tener entre 8 y 100 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        EmpleadoRole role,

        UUID departamentoId
) {
    public EmpleadoCreateRequest(
            String nombre,
            String email,
            String telefono,
            String direccion,
            String password,
            EmpleadoRole role
    ) {
        this(nombre, email, telefono, direccion, password, role, null);
    }
}
