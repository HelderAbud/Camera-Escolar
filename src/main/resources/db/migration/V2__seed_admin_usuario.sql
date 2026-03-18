-- Usuário ADMIN inicial para desenvolvimento
-- senha em texto puro: admin123
-- senha_hash é um BCrypt de 'admin123'

INSERT INTO usuario (nome, email, senha_hash, role, criado_em)
VALUES (
    'Administrador',
    'admin@facelogai.local',
    '$2a$10$Dow1bZ2w9JpJrYVQ5PjI2uh2ugHgd6Nq35p3xNmPR9U1FVLtZL1S2',
    'ADMIN',
    CURRENT_TIMESTAMP
)
ON DUPLICATE KEY UPDATE email = email;

