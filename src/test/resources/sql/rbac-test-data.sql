INSERT INTO escola (id, nome, criado_em)
VALUES (1, 'Escola RBAC', CURRENT_TIMESTAMP);

INSERT INTO camera (id, escola_id, nome, endpoint_url, ativo, criado_em)
VALUES (1, 1, 'Camera RBAC', 'http://example.com/cam-rbac', TRUE, CURRENT_TIMESTAMP);

INSERT INTO aluno (id, nome, matricula, criado_em)
VALUES (1, 'Aluno RBAC', 'MATR-RBAC-1', CURRENT_TIMESTAMP);

INSERT INTO turma (id, escola_id, nome, serie, criado_em)
VALUES (1, 1, 'Turma RBAC', '1A', CURRENT_TIMESTAMP);
