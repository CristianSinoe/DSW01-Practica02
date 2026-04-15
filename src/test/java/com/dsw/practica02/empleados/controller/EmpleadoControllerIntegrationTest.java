package com.dsw.practica02.empleados.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw.practica02.empleados.AbstractIntegrationTest;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class EmpleadoControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/empleados";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @AfterEach
    void cleanUp() {
        empleadoRepository.deleteAll();
    }

    @Test
    void createEmpleado_shouldReturnCreated() throws Exception {
        String payload = objectMapper.writeValueAsString(new CreatePayload("E-010", "Luis", "Centro", "555", "secreto123"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clave").value("E-010"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createEmpleado_shouldReturnConflictWhenDuplicate() throws Exception {
        String payload = objectMapper.writeValueAsString(new CreatePayload("E-020", "Ana", "Centro", "555", "secreto123"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict());
    }

        @Test
        void createEmpleado_shouldRequireAuthenticationAfterBootstrap() throws Exception {
        String firstPayload = objectMapper.writeValueAsString(new CreatePayload("E-001", "Ana", "Centro", "555", "secreto123"));
        String secondPayload = objectMapper.writeValueAsString(new CreatePayload("E-002", "Luis", "Norte", "556", "secreto123"));

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstPayload))
            .andExpect(status().isCreated());

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(secondPayload))
            .andExpect(status().isUnauthorized());
        }

        @Test
        void createEmpleado_shouldAllowAuthenticatedPostAfterBootstrap() throws Exception {
        String firstPayload = objectMapper.writeValueAsString(new CreatePayload("E-001", "Ana", "Centro", "555", "secreto123"));
        String secondPayload = objectMapper.writeValueAsString(new CreatePayload("E-002", "Luis", "Norte", "556", "secreto123"));

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstPayload))
            .andExpect(status().isCreated());

        mockMvc.perform(post(BASE_URL)
                .with(httpBasic("E-001", "secreto123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(secondPayload))
            .andExpect(status().isCreated());
        }

        @Test
        void listEmpleados_shouldRejectInvalidCredentials() throws Exception {
        String payload = objectMapper.writeValueAsString(new CreatePayload("E-001", "Ana", "Centro", "555", "secreto123"));

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated());

        mockMvc.perform(get(BASE_URL)
                .with(httpBasic("E-001", "invalida")))
            .andExpect(status().isUnauthorized());
        }

        @Test
        void listEmpleados_shouldAllowValidCredentials() throws Exception {
        String payload = objectMapper.writeValueAsString(new CreatePayload("E-001", "Ana", "Centro", "555", "secreto123"));

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated());

        mockMvc.perform(get(BASE_URL)
                .with(httpBasic("E-001", "secreto123")))
            .andExpect(status().isOk());
        }

        @Test
        void listEmpleados_shouldReturnBadRequestForInvalidPagination() throws Exception {
        String payload = objectMapper.writeValueAsString(new CreatePayload("E-001", "Ana", "Centro", "555", "secreto123"));

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated());

        mockMvc.perform(get(BASE_URL)
                .with(httpBasic("E-001", "secreto123"))
                .queryParam("page", "-1")
                .queryParam("size", "10"))
            .andExpect(status().isBadRequest());

        mockMvc.perform(get(BASE_URL)
                .with(httpBasic("E-001", "secreto123"))
                .queryParam("page", "0")
                .queryParam("size", "0"))
            .andExpect(status().isBadRequest());
        }

        @Test
        void listEmpleados_shouldClampSizeTo100() throws Exception {
        String payload = objectMapper.writeValueAsString(new CreatePayload("E-001", "Ana", "Centro", "555", "secreto123"));

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated());

        mockMvc.perform(get(BASE_URL)
                .with(httpBasic("E-001", "secreto123"))
                .queryParam("page", "0")
                .queryParam("size", "500"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(100));
        }

    private record CreatePayload(String clave, String nombre, String direccion, String telefono, String password) {
    }
}
