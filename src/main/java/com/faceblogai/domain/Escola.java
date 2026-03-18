package com.faceblogai.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "escola")
public class Escola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Escola() {
        // JPA
    }

    public Escola(String nome) {
        this.nome = nome;
        this.criadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
