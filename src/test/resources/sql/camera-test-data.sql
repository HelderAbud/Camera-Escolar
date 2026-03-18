INSERT INTO escola (id, nome, criado_em)
VALUES (1, 'Escola SQL', CURRENT_TIMESTAMP);

INSERT INTO camera (id, escola_id, nome, endpoint_url, ativo, criado_em)
VALUES (1, 1, 'Camera SQL', 'http://example.com/cam-sql', TRUE, CURRENT_TIMESTAMP);

