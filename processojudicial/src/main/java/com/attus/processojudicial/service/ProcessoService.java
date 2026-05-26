package com.attus.processojudicial.service;

import com.attus.processojudicial.domain.LogProcesso;
import com.attus.processojudicial.domain.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.dto.AtualizarStatusDTO;
import com.attus.processojudicial.dto.ProcessoRequestDTO;
import com.attus.processojudicial.dto.ProcessoResponseDTO;
import com.attus.processojudicial.exception.ProcessoJaCadastradoException;
import com.attus.processojudicial.exception.ProcessoNotFoundException;
import com.attus.processojudicial.repository.LogProcessoRepository;
import com.attus.processojudicial.repository.ProcessoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessoService {

    private final ProcessoRepository processoRepository;
    private final LogProcessoRepository logProcessoRepository;

    @Transactional
    public ProcessoResponseDTO criar(ProcessoRequestDTO dto) {
        log.info("Criando processo com número: {}", dto.getNumero());

        if (processoRepository.existsByNumero(dto.getNumero())) {
            throw new ProcessoJaCadastradoException(dto.getNumero());
        }

        Processo processo = Processo.builder()
                .numero(dto.getNumero())
                .titulo(dto.getTitulo())
                .tipo(dto.getTipo())
                .descricao(dto.getDescricao())
                .status(dto.getStatus())
                .responsavel(dto.getResponsavel())
                .prazo(dto.getPrazo())
                .build();

        Processo salvo = processoRepository.save(processo);
        log.info("Processo criado com sucesso. ID: {}", salvo.getId());
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<ProcessoResponseDTO> listarTodos() {
        log.info("Listando todos os processos");
        return processoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProcessoResponseDTO buscarPorId(Long id) {
        log.info("Buscando processo por ID: {}", id);
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException(id));
        return toResponseDTO(processo);
    }

    @Transactional(readOnly = true)
    public List<ProcessoResponseDTO> listarPorStatus(StatusProcesso status) {
        log.info("Listando processos por status: {}", status);
        return processoRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProcessoResponseDTO atualizar(Long id, ProcessoRequestDTO dto) {
        log.info("Atualizando processo ID: {}", id);
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException(id));

        StringBuilder alteracoes = new StringBuilder();

        if (!processo.getTitulo().equals(dto.getTitulo())) {
            alteracoes.append(String.format("titulo: '%s' → '%s' | ", processo.getTitulo(), dto.getTitulo()));
        }
        if (!processo.getTipo().equals(dto.getTipo())) {
            alteracoes.append(String.format("tipo: '%s' → '%s' | ", processo.getTipo(), dto.getTipo()));
        }
        if (!processo.getResponsavel().equals(dto.getResponsavel())) {
            alteracoes.append(String.format("responsavel: '%s' → '%s' | ", processo.getResponsavel(), dto.getResponsavel()));
        }
        if (!processo.getPrazo().equals(dto.getPrazo())) {
            alteracoes.append(String.format("prazo: '%s' → '%s' | ", processo.getPrazo(), dto.getPrazo()));
        }
        if (!processo.getStatus().equals(dto.getStatus())) {
            alteracoes.append(String.format("status: '%s' → '%s' | ", processo.getStatus(), dto.getStatus()));
        }

        processo.setTitulo(dto.getTitulo());
        processo.setTipo(dto.getTipo());
        processo.setDescricao(dto.getDescricao());
        processo.setResponsavel(dto.getResponsavel());
        processo.setPrazo(dto.getPrazo());
        processo.setStatus(dto.getStatus());

        Processo atualizado = processoRepository.save(processo);

        if (!alteracoes.isEmpty()) {
            LogProcesso logProcesso = LogProcesso.builder()
                    .processo(atualizado)
                    .statusAnterior(processo.getStatus())
                    .statusNovo(dto.getStatus())
                    .observacao("Campos alterados: " + alteracoes)
                    .usuario("sistema")
                    .build();
            logProcessoRepository.save(logProcesso);
        }

        log.info("Processo ID: {} atualizado. Alterações: {}", id, alteracoes);
        return toResponseDTO(atualizado);
    }

    @Transactional
    public ProcessoResponseDTO atualizarStatus(Long id, AtualizarStatusDTO dto) {
        log.info("Atualizando status do processo ID: {} para {}", id, dto.getStatus());
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException(id));

        StatusProcesso statusAnterior = processo.getStatus();

        processo.setStatus(dto.getStatus());
        processoRepository.save(processo);

        LogProcesso logProcesso = LogProcesso.builder()
                .processo(processo)
                .statusAnterior(statusAnterior)
                .statusNovo(dto.getStatus())
                .observacao(dto.getObservacao())
                .usuario(dto.getUsuario())
                .build();

        logProcessoRepository.save(logProcesso);
        log.info("Status do processo ID: {} alterado de {} para {}", id, statusAnterior, dto.getStatus());
        return toResponseDTO(processo);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando processo ID: {}", id);
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException(id));
        processoRepository.delete(processo);
        log.info("Processo ID: {} deletado com sucesso", id);
    }

    @Transactional(readOnly = true)
    public List<ProcessoResponseDTO> listarProcessosComPrazoVencendo() {
        log.info("Listando processos com prazo vencendo");
        return processoRepository
                .findByPrazoBeforeAndStatusNot(LocalDate.now().plusDays(7), StatusProcesso.ENCERRADO)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private ProcessoResponseDTO toResponseDTO(Processo processo) {
        return ProcessoResponseDTO.builder()
                .id(processo.getId())
                .numero(processo.getNumero())
                .titulo(processo.getTitulo())
                .tipo(processo.getTipo())
                .descricao(processo.getDescricao())
                .status(processo.getStatus())
                .responsavel(processo.getResponsavel())
                .prazo(processo.getPrazo())
                .criadoEm(processo.getCriadoEm())
                .atualizadoEm(processo.getAtualizadoEm())
                .build();
    }
}