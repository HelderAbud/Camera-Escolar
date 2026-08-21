package com.faceblogai.dto;

import com.faceblogai.domain.TurmaAluno;
import java.time.Instant;

public record TurmaAlunoResponse(
        Long id, Long turmaId, Long alunoId, String alunoNome, Instant criadoEm) {

    public static TurmaAlunoResponse from(TurmaAluno vinculo) {
        return new TurmaAlunoResponse(
                vinculo.getId(),
                vinculo.getTurma().getId(),
                vinculo.getAluno().getId(),
                vinculo.getAluno().getNome(),
                vinculo.getCriadoEm());
    }
}
