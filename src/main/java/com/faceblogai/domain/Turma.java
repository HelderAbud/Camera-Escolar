package com.faceblogai.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "turma")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "escola_id", nullable = false)
    private Escola escola;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 50)
    private String serie;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Turma() {
        // JPA
    }

    public Turma(Escola escola, String nome, String serie) {
        this.escola = escola;
        this.nome = nome;
        this.serie = serie;
        this.criadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Escola getEscola() {
        return escola;
    }

    public String getNome() {
        return nome;
    }

    public String getSerie() {
        return serie;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }
}
