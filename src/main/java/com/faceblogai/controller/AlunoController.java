package com.faceblogai.controller;

import com.faceblogai.dto.AlunoResponse;
import com.faceblogai.service.AlunoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public Page<AlunoResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return alunoService.listarPaginado(page, size).map(AlunoResponse::from);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscarPorId(@PathVariable Long id) {
        return alunoService.buscarPorId(id)
                .map(AlunoResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody AlunoRequest request) {
        var aluno = alunoService.criar(request.nome(), request.matricula());
        return ResponseEntity.created(URI.create("/api/alunos/" + aluno.getId()))
                .body(AlunoResponse.from(aluno));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> atualizar(
            @PathVariable Long id, @Valid @RequestBody AlunoUpdateRequest request) {
        return alunoService.atualizar(id, request.nome())
                .map(AlunoResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alunoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public record AlunoRequest(
            @NotBlank String nome,
            @NotBlank String matricula) {}

    public record AlunoUpdateRequest(
            @NotBlank String nome) {}
}
