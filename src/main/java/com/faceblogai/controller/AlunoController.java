package com.faceblogai.controller;

import com.faceblogai.domain.Aluno;
import com.faceblogai.service.AlunoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public List<Aluno> listar() {
        return alunoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        return alunoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Aluno> criar(@Valid @RequestBody AlunoRequest request) {
        Aluno aluno = alunoService.criar(request.nome(), request.matricula());
        return ResponseEntity.created(URI.create("/api/alunos/" + aluno.getId()))
                .body(aluno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizar(
            @PathVariable Long id, @Valid @RequestBody AlunoUpdateRequest request) {
        return alunoService.atualizar(id, request.nome())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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
