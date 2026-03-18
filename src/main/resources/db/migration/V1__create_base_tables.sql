-- V1__create_base_tables.sql
-- Estrutura mínima para iniciar o projeto FaceLogAI

CREATE TABLE IF NOT EXISTS usuario (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(150) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    senha_hash   VARCHAR(255) NOT NULL,
    role         VARCHAR(50)  NOT NULL,
    criado_em    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP   NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS escola (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(200) NOT NULL,
    criado_em    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS camera (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    escola_id    BIGINT       NOT NULL,
    nome         VARCHAR(150) NOT NULL,
    endpoint_url VARCHAR(500) NOT NULL,
    ativo        BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_camera_escola
        FOREIGN KEY (escola_id) REFERENCES escola(id)
);

