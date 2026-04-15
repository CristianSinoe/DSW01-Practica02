package com.dsw.practica02.empleados.controller;

import com.dsw.practica02.empleados.dto.DepartamentoCreateRequest;
import com.dsw.practica02.empleados.dto.DepartamentoResponse;
import com.dsw.practica02.empleados.dto.DepartamentoUpdateRequest;
import com.dsw.practica02.empleados.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departamentos")
@Validated
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @Operation(summary = "Listar departamentos")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public ResponseEntity<Page<DepartamentoResponse>> listAll(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(departamentoService.listDepartamentos(pageable));
    }

    @Operation(summary = "Obtener departamento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(departamentoService.getDepartamentoById(id));
    }

    @Operation(summary = "Crear departamento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Creado"),
            @ApiResponse(responseCode = "409", description = "Conflicto por nombre duplicado")
    })
    @PostMapping
    public ResponseEntity<DepartamentoResponse> create(@Valid @RequestBody DepartamentoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departamentoService.createDepartamento(request));
    }

    @Operation(summary = "Actualizar departamento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "No encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto por nombre duplicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody DepartamentoUpdateRequest request
    ) {
        return ResponseEntity.ok(departamentoService.updateDepartamento(id, request));
    }

    @Operation(summary = "Eliminar departamento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sin contenido"),
            @ApiResponse(responseCode = "404", description = "No encontrado"),
            @ApiResponse(responseCode = "409", description = "En uso")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        departamentoService.deleteDepartamento(id);
        return ResponseEntity.noContent().build();
    }
}
