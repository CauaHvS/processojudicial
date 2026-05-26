package com.attus.processojudicial.dto;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoRequestDTO {

    @NotBlank(message = "O numero do proceso é obrigatório")
    private String numero;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "O tipo é obrigatório")
    private String tipo;

    private String descricao;

    @NotNull(message = "O status é obrigatório")
    private StatusProcesso status;

    @NotBlank(message = "O responsável é obrigatório")
    private String responsavel;

    @NotNull(message = "O prazo é obrigatório")
    @Future(message = "O prazo deve ser uma data futura")
    private LocalDate prazo;
}
