package com.dsw.practica02.empleados.service;

import com.dsw.practica02.empleados.config.EmpleadoFeatureProperties;
import com.dsw.practica02.empleados.domain.Departamento;
import com.dsw.practica02.empleados.domain.Empleado;
import com.dsw.practica02.empleados.dto.EmpleadoCreateRequest;
import com.dsw.practica02.empleados.dto.EmpleadoMapper;
import com.dsw.practica02.empleados.dto.EmpleadoResponse;
import com.dsw.practica02.empleados.dto.EmpleadoUpdateRequest;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import com.dsw.practica02.empleados.service.exception.EmailDuplicadaException;
import com.dsw.practica02.empleados.service.exception.EmpleadoNotFoundException;
import com.dsw.practica02.empleados.service.exception.InvalidPaginationException;
import com.dsw.practica02.empleados.service.exception.InvalidPasswordException;
import com.dsw.practica02.empleados.service.exception.PasswordEncodingException;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoService.class);

    private static final String CREATED_COUNTER = "api.empleados.alta";

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;

    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoService departamentoService;
    private final MeterRegistry meterRegistry;
    private final PasswordEncoder passwordEncoder;
    private final EmpleadoFeatureProperties empleadoFeatureProperties;

    public EmpleadoService(
            EmpleadoRepository empleadoRepository,
            DepartamentoService departamentoService,
            MeterRegistry meterRegistry,
            PasswordEncoder passwordEncoder,
            EmpleadoFeatureProperties empleadoFeatureProperties
    ) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoService = departamentoService;
        this.meterRegistry = meterRegistry;
        this.passwordEncoder = passwordEncoder;
        this.empleadoFeatureProperties = empleadoFeatureProperties;
    }

    @Transactional
    public EmpleadoResponse registerEmpleado(EmpleadoCreateRequest request) {
        String emailNormalizado = normalizeEmail(request.email());
        if (empleadoRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new EmailDuplicadaException(emailNormalizado);
        }
        Empleado empleado = buildEmpleado(request, emailNormalizado);
        Empleado saved = empleadoRepository.save(empleado);
        meterRegistry.counter(CREATED_COUNTER).increment();
        LOGGER.info("Empleado registrado email={} requestId={}", saved.getEmail(), currentRequestId());
        return EmpleadoMapper.toResponse(saved);
    }

    @Transactional
    public Page<EmpleadoResponse> listEmpleados(Pageable pageable) {
        Pageable resolvedPageable = resolvePageable(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isSorted() ? pageable.getSort() : defaultSort()
        );
        return empleadoRepository.findAll(resolvedPageable).map(EmpleadoMapper::toResponse);
    }

    @Transactional
    public Page<EmpleadoResponse> listEmpleados(int page, int size, String[] sortParameters) {
        Pageable pageable = resolvePageable(page, size, resolveSort(sortParameters));
        return empleadoRepository.findAll(pageable).map(EmpleadoMapper::toResponse);
    }

    @Transactional
    public EmpleadoResponse getEmpleadoById(UUID id) {
        Empleado empleado = findByIdOrThrow(id);
        return EmpleadoMapper.toResponse(empleado);
    }

    @Transactional
    public EmpleadoResponse updateEmpleado(UUID id, EmpleadoUpdateRequest request) {
        Empleado empleado = findByIdOrThrow(id);
        String emailNormalizado = normalizeEmail(request.email());
        empleadoRepository.findByEmailIgnoreCase(emailNormalizado)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new EmailDuplicadaException(emailNormalizado);
                });

        empleado.setNombre(normalizeText(request.nombre()));
        empleado.setEmail(emailNormalizado);
        empleado.setDireccion(normalizeText(request.direccion()));
        empleado.setTelefono(normalizeText(request.telefono()));
        empleado.setRole(request.role());
        empleado.setDepartamento(resolveDepartamento(request.departamentoId()));
        empleado.setPassword(resolvePasswordForUpdate(request.password(), empleado.getPassword()));

        Empleado saved = empleadoRepository.save(empleado);
        return EmpleadoMapper.toResponse(saved);
    }

    @Transactional
    public void deleteEmpleado(UUID id) {
        Empleado empleado = findByIdOrThrow(id);
        empleadoRepository.delete(empleado);
    }

    private Empleado buildEmpleado(EmpleadoCreateRequest request, String emailNormalizado) {
        Empleado empleado = new Empleado();
        empleado.setClave(generateClave());
        empleado.setNombre(normalizeText(request.nombre()));
        empleado.setEmail(emailNormalizado);
        empleado.setDireccion(normalizeText(request.direccion()));
        empleado.setTelefono(normalizeText(request.telefono()));
        empleado.setPassword(encodePassword(request.password()));
        empleado.setRole(request.role());
        empleado.setDepartamento(resolveDepartamento(request.departamentoId()));
        return empleado;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }

    private String generateClave() {
        return "E-" + UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
    }

    private Pageable resolvePageable(int page, int size, Sort sort) {
        validatePagination(page, size);
        int boundedSize = Math.min(size, empleadoFeatureProperties.getMaxSize());
        return PageRequest.of(page, boundedSize, sort);
    }

    private Sort resolveSort(String[] sortParameters) {
        if (sortParameters == null || sortParameters.length == 0) {
            return defaultSort();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortParameters.length; i++) {
            String parameter = sortParameters[i];
            if (parameter == null || parameter.isBlank()) {
                continue;
            }

            String property;
            String directionToken = null;

            if (parameter.contains(",")) {
                String[] tokens = parameter.split(",", 2);
                property = tokens[0].trim();
                if (tokens.length > 1) {
                    directionToken = tokens[1].trim();
                }
            } else {
                property = parameter.trim();
                if (i + 1 < sortParameters.length && isDirectionToken(sortParameters[i + 1])) {
                    directionToken = sortParameters[++i].trim();
                }
            }

            if (property.isBlank()) {
                continue;
            }

            Sort.Direction direction = "desc".equalsIgnoreCase(directionToken)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, property));
        }

        return orders.isEmpty() ? defaultSort() : Sort.by(orders);
    }

    private boolean isDirectionToken(String token) {
        return "asc".equalsIgnoreCase(token) || "desc".equalsIgnoreCase(token);
    }

    private Sort defaultSort() {
        return Sort.by(empleadoFeatureProperties.getDefaultSort()).ascending();
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new InvalidPaginationException("page debe ser mayor o igual a 0");
        }
        if (size <= 0) {
            throw new InvalidPaginationException("size debe ser mayor que 0");
        }
    }

    private String encodePassword(String rawPassword) {
        String normalizedPassword = normalizeText(rawPassword);
        validatePasswordLength(normalizedPassword);
        try {
            return passwordEncoder.encode(normalizedPassword);
        } catch (RuntimeException ex) {
            LOGGER.error("Error codificando password requestId={}", currentRequestId(), ex);
            throw new PasswordEncodingException();
        }
    }

    private String resolvePasswordForUpdate(String rawPassword, String currentEncodedPassword) {
        String normalizedPassword = normalizeText(rawPassword);
        if (normalizedPassword == null || normalizedPassword.isBlank()) {
            return currentEncodedPassword;
        }
        validatePasswordLength(normalizedPassword);
        return encodePassword(normalizedPassword);
    }

    private void validatePasswordLength(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException();
        }
        int length = password.length();
        if (length < MIN_PASSWORD_LENGTH || length > MAX_PASSWORD_LENGTH) {
            throw new InvalidPasswordException();
        }
    }

    private Empleado findByIdOrThrow(UUID id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new EmpleadoNotFoundException(id));
    }

    private Departamento resolveDepartamento(UUID departamentoId) {
        if (departamentoId == null) {
            return null;
        }
        return departamentoService.findByIdOrThrow(departamentoId);
    }

    private String currentRequestId() {
        String requestId = MDC.get("requestId");
        return requestId != null ? requestId : "N/A";
    }
}
