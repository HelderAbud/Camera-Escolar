package com.faceblogai.dto;

import com.faceblogai.domain.Aluno;
import java.time.Instant;

public record AlunoResponse(
        Long id,
        String nome,
        String matricula,
        Instant criadoEm) {

    public static AlunoResponse from(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getMatricula(),
                aluno.getCriadoEm());
    }
}

