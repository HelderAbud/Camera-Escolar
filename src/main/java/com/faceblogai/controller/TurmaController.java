package com.faceblogai.controller;

import com.faceblogai.domain.CameraTurma;
import com.faceblogai.domain.Turma;
import com.faceblogai.domain.TurmaAluno;
import com.faceblogai.service.TurmaService;
import com.faceblogai.service.VinculoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
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
    public List<Turma> listarPorEscola(@PathVariable Long escolaId) {
        return turmaService.listarPorEscola(escolaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turma> buscarPorId(@PathVariable Long id) {
        return turmaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Turma> criar(@Valid @RequestBody TurmaRequest request) {
        Turma turma = turmaService.criar(request.escolaId(), request.nome(), request.serie());
        return ResponseEntity.created(URI.create("/api/turmas/" + turma.getId()))
                .body(turma);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turma> atualizar(
            @PathVariable Long id, @Valid @RequestBody TurmaUpdateRequest request) {
        return turmaService.atualizar(id, request.nome(), request.serie())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        turmaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Vínculo aluno-turma

    @PostMapping("/{turmaId}/alunos/{alunoId}")
    public TurmaAluno vincularAluno(
            @PathVariable Long turmaId, @PathVariable Long alunoId) {
        return vinculoService.vincularAlunoEmTurma(turmaId, alunoId);
    }

    @DeleteMapping("/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<Void> desvincularAluno(
            @PathVariable Long turmaId, @PathVariable Long alunoId) {
        vinculoService.desvincularAlunoDeTurma(turmaId, alunoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{turmaId}/alunos")
    public List<TurmaAluno> listarAlunosDaTurma(@PathVariable Long turmaId) {
        return vinculoService.listarAlunosDaTurma(turmaId);
    }

    // Vínculo camera-turma

    @PostMapping("/{turmaId}/cameras/{cameraId}")
    public CameraTurma vincularCamera(
            @PathVariable Long turmaId, @PathVariable Long cameraId) {
        return vinculoService.vincularCameraEmTurma(turmaId, cameraId);
    }

    @DeleteMapping("/{turmaId}/cameras/{cameraId}")
    public ResponseEntity<Void> desvincularCamera(
            @PathVariable Long turmaId, @PathVariable Long cameraId) {
        vinculoService.desvincularCameraDeTurma(turmaId, cameraId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{turmaId}/cameras")
    public List<CameraTurma> listarCamerasDaTurma(@PathVariable Long turmaId) {
        return vinculoService.listarCamerasDaTurma(turmaId);
    }

    public record TurmaRequest(
            @NotNull Long escolaId,
            @NotBlank String nome,
            String serie) {}

    public record TurmaUpdateRequest(
            @NotBlank String nome,
            String serie) {}
}
