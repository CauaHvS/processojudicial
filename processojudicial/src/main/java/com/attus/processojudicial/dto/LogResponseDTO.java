package com.attus.processojudicial.dto;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogResponseDTO {

    private Long id;
    private Long processoId;
    private StatusProcesso statusAnterior;
    private StatusProcesso statusNovo;
    private String observacao;
    private String usuario;
    private LocalDateTime realizadoEm;
}