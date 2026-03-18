package com.faceblogai.dto;

import com.faceblogai.domain.Escola;
import java.time.Instant;

public record EscolaResponse(
        Long id,
        String nome,
        Instant criadoEm) {

    public static EscolaResponse from(Escola escola) {
        return new EscolaResponse(
                escola.getId(),
                escola.getNome(),
                escola.getCriadoEm());
    }
}

