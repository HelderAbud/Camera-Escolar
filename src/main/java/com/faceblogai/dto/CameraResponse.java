package com.faceblogai.dto;

import com.faceblogai.domain.Camera;
import java.time.Instant;

public record CameraResponse(
        Long id,
        Long escolaId,
        String escolaNome,
        String nome,
        String endpointUrl,
        boolean ativo,
        Instant criadoEm) {

    public static CameraResponse from(Camera camera) {
        return new CameraResponse(
                camera.getId(),
                camera.getEscola().getId(),
                camera.getEscola().getNome(),
                camera.getNome(),
                camera.getEndpointUrl(),
                camera.isAtivo(),
                camera.getCriadoEm());
    }
}

