package com.attus.processojudicial.controller;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.dto.ProcessoRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ProcessoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private ProcessoRequestDTO buildRequest(String numero) {
        return ProcessoRequestDTO.builder()
                .numero(numero)
                .titulo("Execução Fiscal - Município de BH")
                .tipo("Execução Fiscal")
                .descricao("Cobrança de IPTU")
                .status(StatusProcesso.EM_ANDAMENTO)
                .responsavel("Dr. João Silva")
                .prazo(LocalDate.of(2027, 12, 31))
                .build();
    }

    @Test
    @DisplayName("POST /api/processos - deve criar processo e retornar 201")
    void deveCriarProcesso() throws Exception {
        mockMvc.perform(post("/api/processos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("INT-0001/2026"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("INT-0001/2026"))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    @DisplayName("POST /api/processos - deve retornar 409 para número duplicado")
    void deveRetornar409ParaNumeroDuplicado() throws Exception {
        mockMvc.perform(post("/api/processos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("INT-0002/2026"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/processos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("INT-0002/2026"))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/processos - deve listar processos")
    void deveListarProcessos() throws Exception {
        mockMvc.perform(post("/api/processos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest("INT-0003/2026"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/processos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/processos/{id} - deve retornar 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/processos/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/processos/{id} - deve retornar 404 para ID inexistente")
    void deveRetornar404AoDeletarInexistente() throws Exception {
        mockMvc.perform(delete("/api/processos/99999"))
                .andExpect(status().isNotFound());
    }
}