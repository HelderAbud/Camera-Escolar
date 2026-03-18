package com.faceblogai.controller;

import com.faceblogai.dto.EscolaResponse;
import com.faceblogai.service.EscolaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/escolas")
public class EscolaController {

    private final EscolaService escolaService;

    public EscolaController(EscolaService escolaService) {
        this.escolaService = escolaService;
    }

    @GetMapping
    public Page<EscolaResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return escolaService.listarPaginado(page, size).map(EscolaResponse::from);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscolaResponse> buscarPorId(@PathVariable Long id) {
        return escolaService.buscarPorId(id)
                .map(EscolaResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EscolaResponse> criar(@Valid @RequestBody EscolaRequest request) {
        var escola = escolaService.criar(request.nome());
        return ResponseEntity.created(URI.create("/api/escolas/" + escola.getId()))
                .body(EscolaResponse.from(escola));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscolaResponse> atualizar(
            @PathVariable Long id, @Valid @RequestBody EscolaRequest request) {
        return escolaService.atualizar(id, request.nome())
                .map(EscolaResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        escolaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public record EscolaRequest(@NotBlank String nome) {}
}
