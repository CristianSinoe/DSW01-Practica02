package com.dsw.practica02.empleados.service;

import com.dsw.practica02.empleados.domain.Empleado;
import com.dsw.practica02.empleados.dto.LoginResponse;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final EmpleadoRepository empleadoRepository;

    public AuthService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public LoginResponse getCurrentUser(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Credenciales invalidas");
        }
        Empleado empleado = empleadoRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Credenciales invalidas"));
        return new LoginResponse(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getEmail(),
                empleado.getRole()
        );
    }

    private String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
