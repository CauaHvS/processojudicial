package com.attus.processojudicial.repository;

import com.attus.processojudicial.domain.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {

    Optional<Processo> findByNumero(String numero);

    List<Processo> findByStatus(StatusProcesso status);

    List<Processo> findByResponsavel(String responsavel);

    List<Processo> findByPrazoBeforeAndStatusNot(LocalDate data, StatusProcesso status);

    boolean existsByNumero(String numero);
}
