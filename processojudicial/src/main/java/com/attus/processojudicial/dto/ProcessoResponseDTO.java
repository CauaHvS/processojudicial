package com.attus.processojudicial.dto;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoResponseDTO {

    private Long id;
    private String numero;
    private String titulo;
    private String tipo;
    private String descricao;
    private StatusProcesso status;
    private String responsavel;
    private LocalDate prazo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
