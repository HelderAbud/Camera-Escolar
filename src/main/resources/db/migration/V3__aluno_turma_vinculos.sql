-- V3__aluno_turma_vinculos.sql
-- Entidades de aluno, turma e vínculos com câmeras

CREATE TABLE IF NOT EXISTS aluno (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(150) NOT NULL,
    matricula    VARCHAR(50)  NOT NULL UNIQUE,
    criado_em    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS turma (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    escola_id    BIGINT       NOT NULL,
    nome         VARCHAR(150) NOT NULL,
    serie        VARCHAR(50)  NULL,
    criado_em    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_turma_escola
        FOREIGN KEY (escola_id) REFERENCES escola(id)
);

CREATE TABLE IF NOT EXISTS turma_aluno (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    turma_id     BIGINT NOT NULL,
    aluno_id     BIGINT NOT NULL,
    criado_em    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_turma_aluno_turma
        FOREIGN KEY (turma_id) REFERENCES turma(id),
    CONSTRAINT fk_turma_aluno_aluno
        FOREIGN KEY (aluno_id) REFERENCES aluno(id),
    CONSTRAINT uk_turma_aluno UNIQUE (turma_id, aluno_id)
);

CREATE TABLE IF NOT EXISTS camera_turma (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    camera_id    BIGINT NOT NULL,
    turma_id     BIGINT NOT NULL,
    criado_em    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_camera_turma_camera
        FOREIGN KEY (camera_id) REFERENCES camera(id),
    CONSTRAINT fk_camera_turma_turma
        FOREIGN KEY (turma_id) REFERENCES turma(id),
    CONSTRAINT uk_camera_turma UNIQUE (camera_id, turma_id)
);
