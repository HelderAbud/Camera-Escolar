# Trilha dia a dia — FaceLogAI (Camera Escolar)

> **Metodologia:** [Helder Method v1.2](../../Agentes/helder-method-v1.2-resumo-compartilhavel.md) + [skills-pessoal](../../Agentes/skills-pessoal/skills-pessoal/README-pt_br.md) ([WORKFLOW](../../Agentes/skills-pessoal/skills-pessoal/WORKFLOW.md))  
> **Iniciativa:** Portfólio backend #2 — apresentação + CI + deploy Swagger opcional  
> **Triagem Helder:** **Normal** (CI, LICENSE, deploy; API já estável)  
> **Estado atual:** B0–B2 + P1 concluídos ([`docs/BACKLOG.md`](docs/BACKLOG.md))  
> **Custo:** R$ 0  

**Repositório:** https://github.com/HelderAbud/Camera-Escolar

---

## Como usar

1. Dias 1–7 = **prioridade** (apresentação + CI).
2. Dias 8–14 = deploy opcional.
3. Planos: `.cursor/plans/plan-YYYY-MM-DD-facelogai-*.md`.
4. Validação: `docs/grill-logs/validation-YYYY-MM-DD-*.md` + `slice-verification`.
5. Dias só doc/README = Fast path; CI/código = Core (`tdd` quando houver regra).
6. Matriz de permissões = contrato — não expandir API sem atualizar README.
7. Commit/push/PR só com pedido explícito (HITL).

### Helder → skills-pessoal

| Trilha Helder | Caminho |
|---------------|---------|
| **Simple** | Fast path: fazer → verificar → resumir (dias só documentação) |
| **Normal** | `to-spec` → `to-issues` → build/`tdd` → `slice-verification` → `code-review` |
| **Complex** | igual Normal + HITL entre fases |
| **Hotfix** | `diagnose` → patch mínimo → regressão → só então retomar |

### Core Workflow (mapa)

| Fase | Skill |
|------|-------|
| Spec | `to-spec` |
| Plan | `to-issues` |
| Branch | `git-workflow-and-versioning` |
| Build | `tdd` (código/CI); doc = checklist |
| Verify | `slice-verification` |
| Review | `code-review` |
| Simplify | `code-simplification` |
| Ship | `finishing-a-development-branch` |

### Gates HITL

- `FACELOGAI_SEED_ADMIN_*` em produção (manter desligado)
- Push com credenciais ou `.env`
- Mudança matriz de permissões (contrato API)
- Commit, push ou PR

---

## Visão (14 dias)

| Fase | Dias | Foco |
|------|------|------|
| A — Apresentação | 1–4 | README, screenshots, etapas, LICENSE |
| B — CI | 5–6 | GitHub Actions + badge |
| C — Deploy opcional | 7–10 | Render/Railway + MySQL |
| D — Portfólio | 11–14 | LinkedIn, validation, backlog futuro |

---

## Fase A — Apresentação

### Dia 1 — README sem placeholders 📋

**Trilha:** Simple

**Tarefas**
- [x] Substituir `[PREENCHER_URL_SWAGGER_OU_APP_OU_"em breve"]` por URL real ou *"Demo: em breve — ver Dia 8"*
- [x] Revisar matriz de permissões vs código atual
- [x] Link para [`docs/BACKLOG.md`](docs/BACKLOG.md) com status B2 ✅

**Validação:** zero `[PREENCHER]` críticos no README.

**Evidência:** `docs/grill-logs/validation-2026-07-09-trilha-dia-1.md`

**Prompt Cursor**
```text
FaceLogAI Dia 1 — limpar README placeholders, trilha Simple, fast path / to-issues,
não alterar regras de segurança sem listar impacto.
```

---

### Dia 2 — Screenshots (3 mínimo)

- [x] `docs/screenshots/swagger.png` — Swagger UI
- [x] `docs/screenshots/eventos-filtro.png` — GET `/api/eventos` com filtros
- [x] `docs/screenshots/permissions-matrix.png` — tabela do README ou doc

**Validação:** README secção Screenshots preenchida.

**Evidência:** `docs/grill-logs/validation-2026-07-27-trilha-dia-2.md`

---

### Dia 3 — `docs/portfolio/etapas.md` 📋

**6 etapas sugeridas**
1. Setup Spring Boot + Flyway + MySQL  
2. JWT + perfis (ADMIN, COORDENAÇÃO, PROFESSOR)  
3. CRUD escolas, turmas, alunos, câmeras  
4. Eventos de monitoramento + filtros paginados  
5. Problem Details (RFC 7807) + logs JSON  
6. CI + deploy *(atualizar dia 8)*  

Tags por etapa: `JWT`, `Flyway`, `REST`, `Observability`.

- [x] Criar `docs/portfolio/etapas.md` com as 6 etapas e estados (B0–P1 feitos; CI/deploy pendente)
- [x] Linkar no README (Documentação adicional)

**Validação:** ficheiro existe e resume a ordem lógica do MVP.

**Evidência:** `docs/grill-logs/validation-2026-07-27-trilha-dia-3.md`

---

### Dia 4 — Diagrama domínio + LICENSE

- [x] Mermaid: Escola → Turma → Aluno; Camera → Evento
- [x] Adicionar `LICENSE` (MIT recomendado)
- [x] Atualizar bloco LinkedIn no README (B2 feito)

**DoD Fase A:** README + etapas + 3 screenshots + LICENSE.

**Evidência:** `docs/grill-logs/validation-2026-07-27-trilha-dia-4.md`

---

## Fase B — CI

### Dia 5 — GitHub Actions 📋

| Trilha | Normal |

**Tarefas**
- [x] Criar `.github/workflows/ci.yml`: checkout → Java 21 → `mvn -B test`
- [x] Rodar local: `mvn clean test` antes de push
- [ ] Plano: `.cursor/plans/plan-YYYY-MM-DD-ci-facelogai.md`

**Validação:** workflow verde no GitHub após o primeiro push da branch (HITL).

---

### Dia 6 — Badge + README

- [x] Badge CI no README
- [x] Documentar comando teste em README (já parcialmente existe)
- [x] `docs/grill-logs/validation-2026-08-21-review-fatias.md`

---

## Fase C — Deploy opcional (R$ 0)

### Dia 7 — Plano deploy 📋

- [ ] Escolher Render ou Railway
- [ ] MySQL managed ou Postgres (migrar só se plano aprovar — senão MySQL)
- [ ] **HITL:** seed admin **OFF** em prod
- [ ] Plano rollback: variáveis env documentadas

---

### Dia 8 — Deploy API

- [ ] Deploy container ou jar
- [ ] Health `/health` público
- [ ] Swagger URL no README

---

### Dia 9 — Smoke produção

- [ ] Login com usuário criado manualmente (não seed automático)
- [ ] POST `/api/eventos` + GET filtrado
- [ ] Atualizar etapa 6 em `docs/portfolio/etapas.md`

---

### Dia 10 — Segurança deploy

- [ ] Revisar SecurityConfig — Swagger exposto?
- [ ] JWT secret forte só em env
- [ ] Validation deploy

---

## Fase D — Fechamento portfólio

### Dia 11 — Testes gap (se necessário)

- [ ] 1 MockMvc por controller crítico se cobertura fraca
- [ ] Trilha Simple ou Hotfix se for bug

---

### Dia 12 — `docs/ENTREVISTAS.md` (criar)

- [ ] 3 perguntas + respostas: JWT perfis, eventos filtrados, Problem Details
- [ ] Link no README

---

### Dia 13 — LinkedIn

- [ ] Post backend escolar — diferencial vs LojApp (domínio educação)
- [ ] Link GitHub + Swagger se no ar

---

### Dia 14 — Validation trilha completa

- [ ] Rubrica Helder Normal: CI + docs + deploy ou "local only" honesto
- [ ] `docs/grill-logs/validation-YYYY-MM-DD-trilha-completa.md`
- [ ] Atualizar [`docs/BACKLOG.md`](docs/BACKLOG.md) — marcar itens doc/CI

---

## Backlog futuro (não nesta trilha)

| Item | Quando |
|------|--------|
| Frontend React listagem eventos | Após LojApp + HH no ar |
| Webhook câmera simulado | Normal — plano separado |
| Visão computacional | Complex — spike só em artefato |

---

## Prompt base Cursor

```text
FaceLogAI — Dia N do TRILHA-DIA-A-DIA.md.
Backend MVP B2+P1 já entregue. Foco apresentação/CI/deploy.
Helder [Simple|Normal|Complex|Hotfix] + skills-pessoal.
Doc: fast path. CI/código: to-spec → to-issues → tdd se regra → slice-verification.
Não expandir API sem atualizar matriz permissões no README. HITL em secrets/commit.
```

---

*Trilha v1.1 — 2026-07-09 — Helder v1.2 + skills-pessoal*
