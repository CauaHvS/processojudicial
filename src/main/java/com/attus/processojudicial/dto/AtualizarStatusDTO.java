package com.attus.processojudicial.dto;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarStatusDTO {

    @NotNull(message = "O novo status é obrigatório")
    private StatusProcesso status;

    private String observacao;

    private String usuario;
}
