package com.dsw.practica02.empleados.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dsw.practica02.empleados.config.EmpleadoFeatureProperties;
import com.dsw.practica02.empleados.domain.Empleado;
import com.dsw.practica02.empleados.domain.EmpleadoRole;
import com.dsw.practica02.empleados.dto.EmpleadoCreateRequest;
import com.dsw.practica02.empleados.dto.EmpleadoResponse;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import com.dsw.practica02.empleados.service.exception.EmailDuplicadaException;
import com.dsw.practica02.empleados.service.exception.InvalidPaginationException;
import com.dsw.practica02.empleados.service.exception.PasswordEncodingException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private DepartamentoService departamentoService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmpleadoFeatureProperties empleadoFeatureProperties;

    @InjectMocks
    private EmpleadoService empleadoService;

    private EmpleadoCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new EmpleadoCreateRequest(
                "Ana",
                "ana@empresa.com",
                "555",
                "Centro",
                "secreto123",
                EmpleadoRole.EMPLEADO
        );
    }

    @Test
    void registerEmpleado_shouldPersistAndReturnResponse() {
        when(empleadoRepository.existsByEmailIgnoreCase("ana@empresa.com")).thenReturn(false);
        when(meterRegistry.counter("api.empleados.alta")).thenReturn(mock(Counter.class));
        when(passwordEncoder.encode("secreto123")).thenReturn("{noop}secreto123");
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(invocation -> {
            Empleado entity = invocation.getArgument(0);
            entity.setId(java.util.UUID.randomUUID());
            entity.setCreatedAt(OffsetDateTime.now());
            entity.setUpdatedAt(OffsetDateTime.now());
            return entity;
        });

        EmpleadoResponse response = empleadoService.registerEmpleado(createRequest);

        assertThat(response.nombre()).isEqualTo("Ana");
        assertThat(response.email()).isEqualTo("ana@empresa.com");

        ArgumentCaptor<Empleado> empleadoCaptor = ArgumentCaptor.forClass(Empleado.class);
        verify(empleadoRepository).save(empleadoCaptor.capture());
        assertThat(empleadoCaptor.getValue().getPassword()).isEqualTo("{noop}secreto123");
    }

    @Test
    void registerEmpleado_shouldThrowWhenClaveExists() {
        when(empleadoRepository.existsByEmailIgnoreCase("ana@empresa.com")).thenReturn(true);

        assertThatThrownBy(() -> empleadoService.registerEmpleado(createRequest))
            .isInstanceOf(EmailDuplicadaException.class);
    }

    @Test
    void listEmpleados_shouldApplyDefaultSortByEmailWhenPageableHasNoSort() {
        when(empleadoFeatureProperties.getDefaultSort()).thenReturn("email");
        when(empleadoFeatureProperties.getMaxSize()).thenReturn(100);

        Pageable pageable = PageRequest.of(0, 5);
        when(empleadoRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(java.util.List.of(), pageable, 0));

        Page<EmpleadoResponse> response = empleadoService.listEmpleados(pageable);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(empleadoRepository).findAll(captor.capture());
        Pageable usedPageable = captor.getValue();

        assertThat(response.getTotalElements()).isZero();
        assertThat(usedPageable.getPageNumber()).isEqualTo(0);
        assertThat(usedPageable.getPageSize()).isEqualTo(5);
        assertThat(usedPageable.getSort().getOrderFor("email")).isNotNull();
    }

    @Test
    void registerEmpleado_shouldFailFastWhenPasswordEncodingFails() {
        when(empleadoRepository.existsByEmailIgnoreCase("ana@empresa.com")).thenReturn(false);
        when(passwordEncoder.encode("secreto123")).thenThrow(new IllegalStateException("encoder failure"));

        assertThatThrownBy(() -> empleadoService.registerEmpleado(createRequest))
                .isInstanceOf(PasswordEncodingException.class);
    }

    @Test
    void listEmpleados_shouldClampSizeToMax() {
        when(empleadoFeatureProperties.getMaxSize()).thenReturn(100);

        when(empleadoRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        empleadoService.listEmpleados(0, 500, new String[]{"email,asc"});

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(empleadoRepository).findAll(captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(100);
    }

    @Test
    void listEmpleados_shouldParseSortDirectionProvidedAsSplitRequestParams() {
        when(empleadoFeatureProperties.getMaxSize()).thenReturn(100);
        when(empleadoRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        empleadoService.listEmpleados(0, 10, new String[]{"email", "asc"});

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(empleadoRepository).findAll(captor.capture());

        Sort.Order order = captor.getValue().getSort().getOrderFor("email");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(captor.getValue().getSort().getOrderFor("asc")).isNull();
    }

    @Test
    void listEmpleados_shouldThrowWhenPageIsNegative() {
        assertThatThrownBy(() -> empleadoService.listEmpleados(-1, 10, new String[]{"email,asc"}))
                .isInstanceOf(InvalidPaginationException.class)
                .hasMessageContaining("page");
    }

    @Test
    void listEmpleados_shouldThrowWhenSizeIsInvalid() {
        assertThatThrownBy(() -> empleadoService.listEmpleados(0, 0, new String[]{"email,asc"}))
                .isInstanceOf(InvalidPaginationException.class)
                .hasMessageContaining("size");
    }
}
