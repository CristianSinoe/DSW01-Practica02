package com.dsw.practica02.empleados.config;

import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import com.dsw.practica02.empleados.domain.EmpleadoRole;
import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
public class BootstrapEmpleadoAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final String ADMIN_ROLE_AUTHORITY = "ROLE_ADMIN";

    private final EmpleadoRepository empleadoRepository;

    public BootstrapEmpleadoAuthorizationManager(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authenticationSupplier,
            RequestAuthorizationContext context
    ) {
        if (!empleadoRepository.existsByRole(EmpleadoRole.ADMIN)) {
            return new AuthorizationDecision(true);
        }

        Authentication authentication = authenticationSupplier.get();
        boolean isAuthorized = authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> ADMIN_ROLE_AUTHORITY.equals(grantedAuthority.getAuthority()));

        return new AuthorizationDecision(isAuthorized);
    }
}