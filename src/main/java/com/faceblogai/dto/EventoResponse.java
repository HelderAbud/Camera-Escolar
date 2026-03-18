package com.faceblogai.dto;

import com.faceblogai.domain.EventoMonitoramento;
import java.time.Instant;

public record EventoResponse(
        Long id,
        Long cameraId,
        String cameraNome,
        Long turmaId,
        String turmaNome,
        Long alunoId,
        String alunoNome,
        String tipoEvento,
        String detalhes,
        Instant criadoEm) {

    public static EventoResponse from(EventoMonitoramento e) {
        return new EventoResponse(
                e.getId(),
                e.getCamera().getId(),
                e.getCamera().getNome(),
                e.getTurma() == null ? null : e.getTurma().getId(),
                e.getTurma() == null ? null : e.getTurma().getNome(),
                e.getAluno() == null ? null : e.getAluno().getId(),
                e.getAluno() == null ? null : e.getAluno().getNome(),
                e.getTipoEvento() == null ? null : e.getTipoEvento().name(),
                e.getDetalhes(),
                e.getCriadoEm());
    }
}

