package com.dsw.practica02.empleados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser valido")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        String password
) {
}
