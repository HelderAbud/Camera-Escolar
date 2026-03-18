package com.faceblogai.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "camera_turma")
public class CameraTurma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id", nullable = false)
    private Camera camera;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected CameraTurma() {
        // JPA
    }

    public CameraTurma(Camera camera, Turma turma) {
        this.camera = camera;
        this.turma = turma;
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

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
