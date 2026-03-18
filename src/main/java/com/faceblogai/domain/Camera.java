package com.faceblogai.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "camera")
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "escola_id", nullable = false)
    private Escola escola;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "endpoint_url", nullable = false, length = 500)
    private String endpointUrl;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Camera() {
        // JPA
    }

    public Camera(Escola escola, String nome, String endpointUrl) {
        this.escola = escola;
        this.nome = nome;
        this.endpointUrl = endpointUrl;
        this.ativo = true;
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

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setEscola(Escola escola) {
        this.escola = escola;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
