INSERT INTO escola (id, nome, criado_em)
VALUES (1, 'Escola SQL Evento', CURRENT_TIMESTAMP);

INSERT INTO camera (id, escola_id, nome, endpoint_url, ativo, criado_em)
VALUES (1, 1, 'Camera SQL Evento', 'http://example.com/cam-evento', TRUE, CURRENT_TIMESTAMP);

INSERT INTO aluno (id, nome, matricula, criado_em)
VALUES (1, 'Aluno SQL Evento', 'MATR-TEST-1', CURRENT_TIMESTAMP);

INSERT INTO turma (id, escola_id, nome, serie, criado_em)
VALUES (1, 1, 'Turma SQL Evento', '1A', CURRENT_TIMESTAMP);

INSERT INTO evento_monitoramento (
    id,
    camera_id,
    turma_id,
    aluno_id,
    tipo_evento,
    detalhes,
    criado_em
)
VALUES (1, 1, 1, 1, 'AUSENCIA_DETECTADA', 'Evento inicial (SQL)', CURRENT_TIMESTAMP);

