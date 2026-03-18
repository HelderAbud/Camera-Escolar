package com.faceblogai.service;

import com.faceblogai.domain.*;
import com.faceblogai.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class EventoMonitoramentoService {

    private final EventoMonitoramentoRepository eventoRepository;
    private final CameraRepository cameraRepository;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;

    public EventoMonitoramentoService(
            EventoMonitoramentoRepository eventoRepository,
            CameraRepository cameraRepository,
            TurmaRepository turmaRepository,
            AlunoRepository alunoRepository) {
        this.eventoRepository = eventoRepository;
        this.cameraRepository = cameraRepository;
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public EventoMonitoramento registrar(
            Long cameraId,
            Long turmaId,
            Long alunoId,
            TipoEvento tipoEvento,
            String detalhes) {
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("Câmera não encontrada"));

        Turma turma = turmaId == null ? null : turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));

        Aluno aluno = alunoId == null ? null : alunoRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        EventoMonitoramento evento =
                new EventoMonitoramento(camera, turma, aluno, tipoEvento, detalhes);
        return eventoRepository.save(evento);
    }

    public Optional<EventoMonitoramento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public List<EventoMonitoramento> listarUltimos100() {
        return eventoRepository.findTop100ByOrderByCriadoEmDesc();
    }

    public List<EventoMonitoramento> listarFiltrado(
            Long cameraId,
            Long turmaId,
            Long alunoId,
            Instant fromTs,
            Instant toTs,
            int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 500));
        Pageable pageable = PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "criadoEm"));
        return eventoRepository.buscarFiltrado(cameraId, turmaId, alunoId, fromTs, toTs, pageable);
    }

    public Page<EventoMonitoramento> listarFiltradoPaginado(
            Long cameraId,
            Long turmaId,
            Long alunoId,
            Instant fromTs,
            Instant toTs,
            int page,
            int size) {
        int safeSize = Math.max(1, Math.min(size, 500));
        Pageable pageable = PageRequest.of(page, safeSize, Sort.by(Sort.Direction.DESC, "criadoEm"));
        return eventoRepository.buscarFiltradoPaginado(cameraId, turmaId, alunoId, fromTs, toTs, pageable);
    }
}
