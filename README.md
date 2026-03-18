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

1. Instale Java 21 e Maven.
2. Na pasta `face-log-ai`:

   ```bash
   mvn spring-boot:run
   ```

3. A API ficará exposta em `http://localhost:8080`.

## Próximos passos sugeridos

- Configurar banco real (MySQL/PostgreSQL) e Flyway.
- Definir backlog funcional em `docs/BACKLOG.md`.
- Criar endpoint de healthcheck e documentação OpenAPI.
- Evoluir para módulos de:
  - gestão de alunos / turmas
  - registro de eventos vindos das câmeras
  - relatórios para coordenação e responsáveis

