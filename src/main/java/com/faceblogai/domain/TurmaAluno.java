package com.faceblogai.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "turma_aluno")
public class TurmaAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected TurmaAluno() {
        // JPA
    }

    public TurmaAluno(Turma turma, Aluno aluno) {
        this.turma = turma;
        this.aluno = aluno;
        this.criadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Turma getTurma() {
        return turma;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
