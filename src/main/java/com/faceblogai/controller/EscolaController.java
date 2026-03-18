package com.faceblogai.controller;

import com.faceblogai.domain.Escola;
import com.faceblogai.service.EscolaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/escolas")
public class EscolaController {

    private final EscolaService escolaService;

    public EscolaController(EscolaService escolaService) {
        this.escolaService = escolaService;
    }

    @GetMapping
    public List<Escola> listar() {
        return escolaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Escola> buscarPorId(@PathVariable Long id) {
        return escolaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Escola> criar(@Valid @RequestBody EscolaRequest request) {
        Escola escola = escolaService.criar(request.nome());
        return ResponseEntity.created(URI.create("/api/escolas/" + escola.getId()))
                .body(escola);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Escola> atualizar(
            @PathVariable Long id, @Valid @RequestBody EscolaRequest request) {
        return escolaService.atualizar(id, request.nome())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        escolaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public record EscolaRequest(@NotBlank String nome) {}
}
