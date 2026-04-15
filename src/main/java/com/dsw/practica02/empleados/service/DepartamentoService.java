package com.dsw.practica02.empleados.service;

import com.dsw.practica02.empleados.domain.Departamento;
import com.dsw.practica02.empleados.dto.DepartamentoCreateRequest;
import com.dsw.practica02.empleados.dto.DepartamentoMapper;
import com.dsw.practica02.empleados.dto.DepartamentoResponse;
import com.dsw.practica02.empleados.dto.DepartamentoUpdateRequest;
import com.dsw.practica02.empleados.repository.DepartamentoRepository;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import com.dsw.practica02.empleados.service.exception.DepartamentoDuplicadoException;
import com.dsw.practica02.empleados.service.exception.DepartamentoEnUsoException;
import com.dsw.practica02.empleados.service.exception.DepartamentoNotFoundException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;

    public DepartamentoService(
            DepartamentoRepository departamentoRepository,
            EmpleadoRepository empleadoRepository
    ) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public DepartamentoResponse createDepartamento(DepartamentoCreateRequest request) {
        String nombre = normalizeNombre(request.nombre());
        if (departamentoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new DepartamentoDuplicadoException(nombre);
        }
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre);
        return DepartamentoMapper.toResponse(departamentoRepository.save(departamento));
    }

    @Transactional
    public Page<DepartamentoResponse> listDepartamentos(Pageable pageable) {
        Pageable resolvedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("nombre").ascending());
        return departamentoRepository.findAll(resolvedPageable).map(DepartamentoMapper::toResponse);
    }

    @Transactional
    public DepartamentoResponse getDepartamentoById(UUID id) {
        return DepartamentoMapper.toResponse(findByIdOrThrow(id));
    }

    @Transactional
    public DepartamentoResponse updateDepartamento(UUID id, DepartamentoUpdateRequest request) {
        Departamento departamento = findByIdOrThrow(id);
        String nombre = normalizeNombre(request.nombre());
        departamentoRepository.findByNombreIgnoreCase(nombre)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DepartamentoDuplicadoException(nombre);
                });
        departamento.setNombre(nombre);
        return DepartamentoMapper.toResponse(departamentoRepository.save(departamento));
    }

    @Transactional
    public void deleteDepartamento(UUID id) {
        Departamento departamento = findByIdOrThrow(id);
        if (empleadoRepository.existsByDepartamentoId(id)) {
            throw new DepartamentoEnUsoException(id);
        }
        departamentoRepository.delete(departamento);
    }

    public Departamento findByIdOrThrow(UUID id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new DepartamentoNotFoundException(id));
    }

    private String normalizeNombre(String nombre) {
        return nombre == null ? null : nombre.trim();
    }
}
