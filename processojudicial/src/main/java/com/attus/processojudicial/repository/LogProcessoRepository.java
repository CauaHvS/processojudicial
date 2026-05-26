package com.attus.processojudicial.repository;

import com.attus.processojudicial.domain.LogProcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogProcessoRepository extends JpaRepository<LogProcesso, Long> {

    List<LogProcesso> findByProcessoIdOrderByRealizadoEmDesc(long processoId);
}
