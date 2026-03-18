package com.faceblogai.controller;

import com.faceblogai.dto.EventoResponse;
import com.faceblogai.service.EventoMonitoramentoService;
import com.faceblogai.domain.TipoEvento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping("/api/eventos")
public class EventoMonitoramentoController {

    private final EventoMonitoramentoService eventoService;

    public EventoMonitoramentoController(EventoMonitoramentoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> registrar(@Valid @RequestBody EventoRequest request) {
        var evento = eventoService.registrar(
                request.cameraId(),
                request.turmaId(),
                request.alunoId(),
                request.tipoEvento(),
                request.detalhes());
        return ResponseEntity
                .created(URI.create("/api/eventos/" + evento.getId()))
                .body(EventoResponse.from(evento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarPorId(@PathVariable Long id) {
        return eventoService.buscarPorId(id)
                .map(EventoResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<EventoResponse> listar(
            @RequestParam(required = false) Long cameraId,
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return eventoService.listarFiltradoPaginado(cameraId, turmaId, alunoId, from, to, page, size)
                .map(EventoResponse::from);
    }

    public record EventoRequest(
            @NotNull Long cameraId,
            Long turmaId,
            Long alunoId,
            @NotNull TipoEvento tipoEvento,
            String detalhes) {}
}
