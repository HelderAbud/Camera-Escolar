# Etapas do projeto — FaceLogAI (Camera Escolar)

Narrativa curta para **portfólio / entrevista**: o que foi construído e em que ordem lógica. Detalhe operacional em [`BACKLOG.md`](../BACKLOG.md) e [`WORKFLOW.md`](../WORKFLOW.md).

| # | Etapa | Tags | Estado |
|---|--------|------|--------|
| 1 | Setup Spring Boot + Flyway + MySQL | `Flyway` | Feito (B0) |
| 2 | JWT + perfis (ADMIN, COORDENACAO, PROFESSOR) | `JWT` | Feito (B1) |
| 3 | CRUD escolas, turmas, alunos, câmeras | `REST` | Feito (B2) |
| 4 | Eventos de monitoramento + filtros paginados | `REST` | Feito (B2) |
| 5 | Problem Details (RFC 7807) + logs JSON | `Observability` | Feito (P1) |
| 6 | CI + deploy | — | CI no repositório (Actions); deploy público pendente (Dia 8) |

---

## 1. Setup Spring Boot + Flyway + MySQL

Base Maven com Spring Boot 3, Web, Security, JPA, Actuator e Springdoc. Health em `/health`. Persistência com MySQL local (`docker compose`) e migrations Flyway (`V1`…`V5`). Swagger UI em `/swagger-ui.html`.

- Como rodar: [`README.md`](../../README.md)
- Migrations: `src/main/resources/db/migration/`

---

## 2. JWT + perfis (ADMIN, COORDENACAO, PROFESSOR)

Login em `POST /api/auth/login` devolve Bearer JWT com email e role. Enum `PerfilUsuario`: `ADMIN`, `COORDENACAO`, `PROFESSOR`. Filtro JWT + `@PreAuthorize` nas mutações; leitura autenticada para os três perfis.

- Matriz: secção no [`README.md`](../../README.md)
- Screenshot: [`../screenshots/permissions-matrix.png`](../screenshots/permissions-matrix.png)

---

## 3. CRUD escolas, turmas, alunos, câmeras

Domínio escolar em camadas Controller → Service → Repository: escolas, câmeras por escola, turmas, alunos e vínculos (aluno↔turma, câmera↔turma).

- Controllers: `EscolaController`, `CameraController`, `TurmaController`, `AlunoController`
- Swagger: [`../screenshots/swagger.png`](../screenshots/swagger.png)

---

## 4. Eventos de monitoramento + filtros paginados

`POST /api/eventos` regista evento ligado a câmera (turma/aluno opcionais). `GET /api/eventos` filtra por `cameraId`, `turmaId`, `alunoId`, `from`, `to` com paginação (`page`, `size`).

- Evidência Dia 2: [`../screenshots/eventos-filtro.png`](../screenshots/eventos-filtro.png)
- Backlog: B2-03 / B2-04 em [`BACKLOG.md`](../BACKLOG.md)

---

## 5. Problem Details (RFC 7807) + logs JSON

Erros via `ApiExceptionHandler` (`ProblemDetail`). Logs estruturados JSON (`logback` + logstash encoder) com `X-Request-Id` e `RequestLoggingFilter` (`method`, `path`, `status`, `durationMs`).

- Marco P1 em [`BACKLOG.md`](../BACKLOG.md)

---

## 6. CI + deploy

GitHub Actions (`.github/workflows/ci.yml`, `mvn -B test`) e badge no README — **feito neste ciclo**. Demo pública (Render/Railway) — **ainda não**. Seguir Dias 8+ em [`TRILHA-DIA-A-DIA.md`](../../TRILHA-DIA-A-DIA.md).

---

## DoD Fase A (apresentação)

| Item | Estado |
|------|--------|
| README sem placeholders críticos | Feito (Dia 1) |
| 3 screenshots | Feito (Dia 2) |
| Este `etapas.md` | Feito (Dia 3) |
| LICENSE MIT | Feito (Dia 4) |
| Diagrama domínio Mermaid | Feito (Dia 4 — [`dominio.md`](dominio.md)) |
