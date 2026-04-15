package com.dsw.practica02.empleados.repository;

import com.dsw.practica02.empleados.domain.Empleado;
import com.dsw.practica02.empleados.domain.EmpleadoRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {

    Optional<Empleado> findByClaveIgnoreCase(String clave);

    Optional<Empleado> findByEmailIgnoreCase(String email);

    boolean existsByClaveIgnoreCase(String clave);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRole(EmpleadoRole role);

    List<Empleado> findAllByOrderByClaveAsc();

    boolean existsByDepartamentoId(UUID departamentoId);
}
