package com.faceblogai.controller;

import com.faceblogai.dto.CameraTurmaResponse;
import com.faceblogai.dto.TurmaAlunoResponse;
import com.faceblogai.dto.TurmaResponse;
import com.faceblogai.service.TurmaService;
import com.faceblogai.service.VinculoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/turmas")
public class TurmaController {

    private final TurmaService turmaService;
    private final VinculoService vinculoService;

    public TurmaController(TurmaService turmaService, VinculoService vinculoService) {
        this.turmaService = turmaService;
        this.vinculoService = vinculoService;
    }

    @GetMapping("/escola/{escolaId}")
    public List<TurmaResponse> listarPorEscola(@PathVariable Long escolaId) {
        return turmaService.listarPorEscola(escolaId).stream().map(TurmaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponse> buscarPorId(@PathVariable Long id) {
        return turmaService
                .buscarPorId(id)
                .map(TurmaResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @PostMapping
    public ResponseEntity<TurmaResponse> criar(@Valid @RequestBody TurmaRequest request) {
        var turma = turmaService.criar(request.escolaId(), request.nome(), request.serie());
        return ResponseEntity.created(URI.create("/api/turmas/" + turma.getId()))
                .body(TurmaResponse.from(turma));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponse> atualizar(
            @PathVariable Long id, @Valid @RequestBody TurmaUpdateRequest request) {
        return turmaService
                .atualizar(id, request.nome(), request.serie())
                .map(TurmaResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        turmaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @PostMapping("/{turmaId}/alunos/{alunoId}")
    public TurmaAlunoResponse vincularAluno(
            @PathVariable Long turmaId, @PathVariable Long alunoId) {
        return TurmaAlunoResponse.from(vinculoService.vincularAlunoEmTurma(turmaId, alunoId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @DeleteMapping("/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<Void> desvincularAluno(
            @PathVariable Long turmaId, @PathVariable Long alunoId) {
        vinculoService.desvincularAlunoDeTurma(turmaId, alunoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{turmaId}/alunos")
    public List<TurmaAlunoResponse> listarAlunosDaTurma(@PathVariable Long turmaId) {
        return vinculoService.listarAlunosDaTurma(turmaId).stream()
                .map(TurmaAlunoResponse::from)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @PostMapping("/{turmaId}/cameras/{cameraId}")
    public CameraTurmaResponse vincularCamera(
            @PathVariable Long turmaId, @PathVariable Long cameraId) {
        return CameraTurmaResponse.from(vinculoService.vincularCameraEmTurma(turmaId, cameraId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @DeleteMapping("/{turmaId}/cameras/{cameraId}")
    public ResponseEntity<Void> desvincularCamera(
            @PathVariable Long turmaId, @PathVariable Long cameraId) {
        vinculoService.desvincularCameraDeTurma(turmaId, cameraId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{turmaId}/cameras")
    public List<CameraTurmaResponse> listarCamerasDaTurma(@PathVariable Long turmaId) {
        return vinculoService.listarCamerasDaTurma(turmaId).stream()
                .map(CameraTurmaResponse::from)
                .toList();
    }

    public record TurmaRequest(
            @NotNull Long escolaId, @NotBlank String nome, String serie) {}

    public record TurmaUpdateRequest(@NotBlank String nome, String serie) {}
}
