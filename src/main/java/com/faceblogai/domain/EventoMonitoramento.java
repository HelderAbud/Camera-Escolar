package com.faceblogai.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "evento_monitoramento")
public class EventoMonitoramento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id", nullable = false)
    private Camera camera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 80)
    private TipoEvento tipoEvento;

    @Lob
    @Column
    private String detalhes;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected EventoMonitoramento() {
        // JPA
    }

    public EventoMonitoramento(
            Camera camera,
            Turma turma,
            Aluno aluno,
            TipoEvento tipoEvento,
            String detalhes) {
        this.camera = camera;
        this.turma = turma;
        this.aluno = aluno;
        this.tipoEvento = tipoEvento;
        this.detalhes = detalhes;
        this.criadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Camera getCamera() {
        return camera;
    }

    public Turma getTurma() {
        return turma;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
