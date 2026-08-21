package com.faceblogai.dto;

import com.faceblogai.domain.Turma;
import java.time.Instant;

public record TurmaResponse(
        Long id, Long escolaId, String escolaNome, String nome, String serie, Instant criadoEm) {

    public static TurmaResponse from(Turma turma) {
        return new TurmaResponse(
                turma.getId(),
                turma.getEscola().getId(),
                turma.getEscola().getNome(),
                turma.getNome(),
                turma.getSerie(),
                turma.getCriadoEm());
    }
}
