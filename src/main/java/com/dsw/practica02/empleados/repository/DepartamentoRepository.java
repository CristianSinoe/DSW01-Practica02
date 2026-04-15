package com.dsw.practica02.empleados.repository;

import com.dsw.practica02.empleados.domain.Departamento;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, UUID> {

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Departamento> findByNombreIgnoreCase(String nombre);
}
