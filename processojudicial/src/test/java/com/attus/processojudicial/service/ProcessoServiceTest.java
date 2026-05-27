package com.attus.processojudicial.service;

import com.attus.processojudicial.domain.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.dto.ProcessoRequestDTO;
import com.attus.processojudicial.dto.ProcessoResponseDTO;
import com.attus.processojudicial.exception.ProcessoJaCadastradoException;
import com.attus.processojudicial.exception.ProcessoNotFoundException;
import com.attus.processojudicial.repository.LogProcessoRepository;
import com.attus.processojudicial.repository.ProcessoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessoServiceTest {

    @Mock
    private ProcessoRepository processoRepository;

    @Mock
    private LogProcessoRepository logProcessoRepository;

    @InjectMocks
    private ProcessoService processoService;

    private Processo processo;
    private ProcessoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        processo = Processo.builder()
                .id(1L)
                .numero("0001/2026")
                .titulo("Execução Fiscal - Município de BH")
                .tipo("Execução Fiscal")
                .descricao("Cobrança de IPTU")
                .status(StatusProcesso.EM_ANDAMENTO)
                .responsavel("Dr. João Silva")
                .prazo(LocalDate.of(2026, 12, 31))
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        requestDTO = ProcessoRequestDTO.builder()
                .numero("0001/2026")
                .titulo("Execução Fiscal - Município de BH")
                .tipo("Execução Fiscal")
                .descricao("Cobrança de IPTU")
                .status(StatusProcesso.EM_ANDAMENTO)
                .responsavel("Dr. João Silva")
                .prazo(LocalDate.of(2026, 12, 31))
                .build();
    }

    @Test
    @DisplayName("Deve criar um processo com sucesso")
    void deveCriarProcessoComSucesso() {
        when(processoRepository.existsByNumero(any())).thenReturn(false);
        when(processoRepository.save(any())).thenReturn(processo);

        ProcessoResponseDTO response = processoService.criar(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getNumero()).isEqualTo("0001/2026");
        verify(processoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar processo com número duplicado")
    void deveLancarExcecaoNumeroDuplicado() {
        when(processoRepository.existsByNumero(any())).thenReturn(true);

        assertThatThrownBy(() -> processoService.criar(requestDTO))
                .isInstanceOf(ProcessoJaCadastradoException.class)
                .hasMessageContaining("0001/2026");

        verify(processoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todos os processos")
    void deveListarTodosProcessos() {
        when(processoRepository.findAll()).thenReturn(List.of(processo));

        List<ProcessoResponseDTO> response = processoService.listarTodos();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getNumero()).isEqualTo("0001/2026");
    }

    @Test
    @DisplayName("Deve buscar processo por ID com sucesso")
    void deveBuscarProcessoPorId() {
        when(processoRepository.findById(1L)).thenReturn(Optional.of(processo));

        ProcessoResponseDTO response = processoService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar processo com ID inexistente")
    void deveLancarExcecaoIdInexistente() {
        when(processoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processoService.buscarPorId(99L))
                .isInstanceOf(ProcessoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve deletar processo com sucesso")
    void deveDeletarProcesso() {
        when(processoRepository.findById(1L)).thenReturn(Optional.of(processo));
        doNothing().when(processoRepository).delete(processo);

        assertThatCode(() -> processoService.deletar(1L))
                .doesNotThrowAnyException();

        verify(processoRepository, times(1)).delete(processo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar processo inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(processoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processoService.deletar(99L))
                .isInstanceOf(ProcessoNotFoundException.class);
    }
}

