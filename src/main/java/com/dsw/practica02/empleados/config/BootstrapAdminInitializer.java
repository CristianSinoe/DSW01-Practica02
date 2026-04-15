package com.dsw.practica02.empleados.config;

import com.dsw.practica02.empleados.domain.Empleado;
import com.dsw.practica02.empleados.domain.EmpleadoRole;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BootstrapAdminInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapAdminInitializer.class);

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public BootstrapAdminInitializer(
            EmpleadoRepository empleadoRepository,
            PasswordEncoder passwordEncoder,
            Environment environment
    ) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (empleadoRepository.existsByRole(EmpleadoRole.ADMIN)) {
            return;
        }

        String email = normalizeEmail(environment.getProperty("ADMIN_EMAIL"));
        String password = normalizeText(environment.getProperty("ADMIN_PASSWORD"));
        String nombre = normalizeText(environment.getProperty("ADMIN_NOMBRE"));
        String telefono = normalizeText(environment.getProperty("ADMIN_TELEFONO"));
        String direccion = normalizeText(environment.getProperty("ADMIN_DIRECCION"));

        List<String> missing = new ArrayList<>();
        if (isBlank(email)) {
            missing.add("ADMIN_EMAIL");
        }
        if (isBlank(password)) {
            missing.add("ADMIN_PASSWORD");
        }
        if (isBlank(nombre)) {
            missing.add("ADMIN_NOMBRE");
        }
        if (isBlank(telefono)) {
            missing.add("ADMIN_TELEFONO");
        }
        if (isBlank(direccion)) {
            missing.add("ADMIN_DIRECCION");
        }

        if (!missing.isEmpty()) {
            LOGGER.warn("Bootstrap admin omitido: faltan variables {}", missing);
            return;
        }

        if (empleadoRepository.existsByEmailIgnoreCase(email)) {
            LOGGER.warn("Bootstrap admin omitido: email ya existe email={}", email);
            return;
        }

        Empleado admin = new Empleado();
        admin.setClave(generateClave());
        admin.setNombre(nombre);
        admin.setEmail(email);
        admin.setTelefono(telefono);
        admin.setDireccion(direccion);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(EmpleadoRole.ADMIN);
        empleadoRepository.save(admin);
        LOGGER.info("Bootstrap admin creado email={}", email);
    }

    private String normalizeEmail(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String generateClave() {
        return "E-" + UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
    }
}
