-- V4__evento_monitoramento.sql
-- Registro de eventos de monitoramento (logs) gerados por câmera/IA

CREATE TABLE IF NOT EXISTS evento_monitoramento (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    camera_id    BIGINT       NOT NULL,
    turma_id     BIGINT       NULL,
    aluno_id     BIGINT       NULL,
    tipo_evento  VARCHAR(80)  NOT NULL,
    detalhes     TEXT         NULL,
    criado_em    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evento_camera
        FOREIGN KEY (camera_id) REFERENCES camera(id),
    CONSTRAINT fk_evento_turma
        FOREIGN KEY (turma_id) REFERENCES turma(id),
    CONSTRAINT fk_evento_aluno
        FOREIGN KEY (aluno_id) REFERENCES aluno(id)
);

CREATE INDEX idx_evento_camera_criado_em ON evento_monitoramento (camera_id, criado_em);
CREATE INDEX idx_evento_turma_criado_em ON evento_monitoramento (turma_id, criado_em);
CREATE INDEX idx_evento_aluno_criado_em ON evento_monitoramento (aluno_id, criado_em);
CREATE INDEX idx_evento_criado_em ON evento_monitoramento (criado_em);
