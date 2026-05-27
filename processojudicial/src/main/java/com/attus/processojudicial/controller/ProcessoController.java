package com.attus.processojudicial.controller;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.dto.AtualizarStatusDTO;
import com.attus.processojudicial.dto.LogResponseDTO;
import com.attus.processojudicial.dto.ProcessoRequestDTO;
import com.attus.processojudicial.dto.ProcessoResponseDTO;
import com.attus.processojudicial.service.ProcessoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/processos")
@RequiredArgsConstructor
public class ProcessoController {

    private final ProcessoService processoService;

    @PostMapping
    public ResponseEntity<ProcessoResponseDTO> criar(@Valid @RequestBody ProcessoRequestDTO dto) {
        log.info("POST /api/processos - Criando processo: {}", dto.getNumero());
        ProcessoResponseDTO response = processoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProcessoResponseDTO>> listarTodos() {
        log.info("GET / api/processos - Listando todos os processos");
        return ResponseEntity.ok(processoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessoResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/processos/{} - Buscando processo", id);
        return ResponseEntity.ok(processoService.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProcessoResponseDTO>> listarPorStatus(@PathVariable StatusProcesso status) {
        log.info("GET /api/processos/status/{} - Listando por status", status);
        return ResponseEntity.ok(processoService.listarPorStatus(status));
    }


    @GetMapping("/prazo-vencendo")
    public ResponseEntity<List<ProcessoResponseDTO>> listarComPrazoVencendo() {
        log.info("GET /api/processos/prazo-vencendo");
        return ResponseEntity.ok(processoService.listarProcessosComPrazoVencendo());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProcessoRequestDTO dto) {
        log.info("PUT /api/processos/{} - Atualizando processo", id);
        return ResponseEntity.ok(processoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProcessoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusDTO dto) {
        log.info("PATCH /api/processos/{}/status - Atualizando status", id);
        return ResponseEntity.ok(processoService.atualizarStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/processos/{} - Deletando processo", id);
        processoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<LogResponseDTO>> buscarLogs(@PathVariable Long id) {
        log.info("GET /api/processos/{}/logs - Buscando logs", id);
        return ResponseEntity.ok(processoService.buscarLogs(id));
    }
}
