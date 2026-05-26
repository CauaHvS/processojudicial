package com.attus.processojudicial.domain;

import com.attus.processojudicial.domain.enums.StatusProcesso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs_processo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogProcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;

    @Enumerated(EnumType.STRING)
    private StatusProcesso statusAnterior;

    @Enumerated(EnumType.STRING)
    private StatusProcesso statusNovo;

    private String observacao;

    private String usuario;

    @Column(nullable = false)
    private LocalDateTime realizadoEm;

    @PrePersist
    public void prePersist() {
        this.realizadoEm = LocalDateTime.now();
    }


}
