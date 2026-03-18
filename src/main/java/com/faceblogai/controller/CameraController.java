package com.faceblogai.controller;

import com.faceblogai.dto.CameraResponse;
import com.faceblogai.service.CameraService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {

    private final CameraService cameraService;

    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping("/escola/{escolaId}")
    public List<CameraResponse> listarPorEscola(@PathVariable Long escolaId) {
        return cameraService.listarPorEscola(escolaId)
                .stream()
                .map(CameraResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CameraResponse> buscarPorId(@PathVariable Long id) {
        return cameraService.buscarPorId(id)
                .map(CameraResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @PostMapping
    public ResponseEntity<CameraResponse> criar(@Valid @RequestBody CameraRequest request) {
        var camera =
                cameraService.criar(request.escolaId(), request.nome(), request.endpointUrl());
        return ResponseEntity.created(URI.create("/api/cameras/" + camera.getId()))
                .body(CameraResponse.from(camera));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @PutMapping("/{id}")
    public ResponseEntity<CameraResponse> atualizar(
            @PathVariable Long id, @Valid @RequestBody CameraUpdateRequest request) {
        return cameraService
                .atualizar(id, request.nome(), request.endpointUrl(), request.ativo())
                .map(CameraResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cameraService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public record CameraRequest(
            @NotNull Long escolaId,
            @NotBlank String nome,
            @Pattern(
                    regexp = "^(rtsp|rtmp|http|https)://.+",
                    message = "endpointUrl deve ser uma URL válida (rtsp, rtmp, http ou https)"
            )
            @NotBlank String endpointUrl) {}

    public record CameraUpdateRequest(
            @NotBlank String nome,
            @Pattern(
                    regexp = "^(rtsp|rtmp|http|https)://.+",
                    message = "endpointUrl deve ser uma URL válida (rtsp, rtmp, http ou https)"
            )
            @NotBlank String endpointUrl,
            @NotNull Boolean ativo) {}
}
