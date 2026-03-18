# FaceLogAI — Monitoramento para Câmera Escolar

Projeto backend em Spring Boot para monitoramento de câmeras em ambiente escolar (FaceLogAI), organizado no padrão dos seus outros projetos Java.

## Stack principal

- Java 21
- Spring Boot 3
- Spring Web, Security, Data JPA
- Flyway (migrações de banco)
- JWT (auth)
- Springdoc OpenAPI (Swagger UI)

## Estrutura de pastas (backend)

- `src/main/java/com/faceblogai`
  - `controller/`
  - `service/`
  - `model/`
  - `repository/`
  - `config/`
- `src/main/resources`
  - `templates/`
  - `static/`
- `docs/`
  - `BACKLOG.md`
  - `WORKFLOW.md`

## Como rodar (local)
### Pré-requisitos
- Java 21
- Maven 3.9+
- Docker (para o banco)

### 1. Subir o banco
```bash
docker compose up -d
```

### 2. Configurar o JWT secret
```bash
# Gerar um secret seguro:
openssl rand -base64 32

# Exportar antes de rodar:
export JWT_SECRET_BASE64=<valor gerado acima>
```

### 3. Rodar a aplicação
```bash
mvn spring-boot:run
```

### 4. Acessar
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/health

### Login inicial (seed de desenvolvimento)
```json
POST /api/auth/login
{ "email": "admin@facelogai.local", "password": "admin123" }
```

## Perfis e permissões

| Endpoint | ADMIN | COORDENACAO | PROFESSOR |
|----------|-------|-------------|-----------|
| GET (leitura geral) | ✓ | ✓ | ✓ |
| POST /api/escolas | ✓ | — | — |
| POST /api/cameras | ✓ | ✓ | — |
| DELETE /api/cameras | ✓ | — | — |
| DELETE /api/alunos | ✓ | ✓ | — |
| POST /api/turmas | ✓ | ✓ | — |
| DELETE /api/turmas | ✓ | — | — |

