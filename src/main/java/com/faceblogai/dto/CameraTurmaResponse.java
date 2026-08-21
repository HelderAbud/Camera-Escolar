package com.faceblogai.dto;

import com.faceblogai.domain.CameraTurma;
import java.time.Instant;

public record CameraTurmaResponse(
        Long id, Long turmaId, Long cameraId, String cameraNome, Instant criadoEm) {

    public static CameraTurmaResponse from(CameraTurma vinculo) {
        return new CameraTurmaResponse(
                vinculo.getId(),
                vinculo.getTurma().getId(),
                vinculo.getCamera().getId(),
                vinculo.getCamera().getNome(),
                vinculo.getCriadoEm());
    }
}
