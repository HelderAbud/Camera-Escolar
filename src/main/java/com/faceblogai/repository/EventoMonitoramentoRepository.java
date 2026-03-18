package com.faceblogai.repository;

import com.faceblogai.domain.EventoMonitoramento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;
import java.time.Instant;

public interface EventoMonitoramentoRepository extends JpaRepository<EventoMonitoramento, Long> {

    List<EventoMonitoramento> findTop100ByOrderByCriadoEmDesc();

    @Query("""
            select e
            from EventoMonitoramento e
            where (:cameraId is null or e.camera.id = :cameraId)
              and (:turmaId is null or e.turma.id = :turmaId)
              and (:alunoId is null or e.aluno.id = :alunoId)
              and (:fromTs is null or e.criadoEm >= :fromTs)
              and (:toTs is null or e.criadoEm <= :toTs)
            order by e.criadoEm desc
            """)
    List<EventoMonitoramento> buscarFiltrado(
            @Param("cameraId") Long cameraId,
            @Param("turmaId") Long turmaId,
            @Param("alunoId") Long alunoId,
            @Param("fromTs") Instant fromTs,
            @Param("toTs") Instant toTs,
            Pageable pageable);

    @Query("""
            select e
            from EventoMonitoramento e
            where (:cameraId is null or e.camera.id = :cameraId)
              and (:turmaId is null or e.turma.id = :turmaId)
              and (:alunoId is null or e.aluno.id = :alunoId)
              and (:fromTs is null or e.criadoEm >= :fromTs)
              and (:toTs is null or e.criadoEm <= :toTs)
            """)
    Page<EventoMonitoramento> buscarFiltradoPaginado(
            @Param("cameraId") Long cameraId,
            @Param("turmaId") Long turmaId,
            @Param("alunoId") Long alunoId,
            @Param("fromTs") Instant fromTs,
            @Param("toTs") Instant toTs,
            Pageable pageable);
}
