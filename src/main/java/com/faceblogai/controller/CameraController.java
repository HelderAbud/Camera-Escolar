package com.faceblogai.controller;

import com.faceblogai.domain.Camera;
import com.faceblogai.service.CameraService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
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
    public List<Camera> listarPorEscola(@PathVariable Long escolaId) {
        return cameraService.listarPorEscola(escolaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Camera> buscarPorId(@PathVariable Long id) {
        return cameraService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Camera> criar(@Valid @RequestBody CameraRequest request) {
        Camera camera =
                cameraService.criar(request.escolaId(), request.nome(), request.endpointUrl());
        return ResponseEntity.created(URI.create("/api/cameras/" + camera.getId()))
                .body(camera);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Camera> atualizar(
            @PathVariable Long id, @Valid @RequestBody CameraUpdateRequest request) {
        return cameraService
                .atualizar(id, request.nome(), request.endpointUrl(), request.ativo())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cameraService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public record CameraRequest(
            @NotNull Long escolaId,
            @NotBlank String nome,
            @NotBlank String endpointUrl) {}

    public record CameraUpdateRequest(
            @NotBlank String nome,
            @NotBlank String endpointUrl,
            @NotNull Boolean ativo) {}
}
