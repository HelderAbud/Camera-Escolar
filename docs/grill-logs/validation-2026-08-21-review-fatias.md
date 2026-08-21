# Validação — FaceLogAI review (fatias RBAC, DTO, CI, docs)

**Data:** 2026-08-21  
**Trilha:** Complex (RBAC) + Normal (CI) + Simple (docs)  
**Comando:** `mvn clean test` (H2; sem Docker neste PC)  
**Resultado:** BUILD SUCCESS — **30** testes, 0 falhas (inclui `RbacControllerTest`, `TurmaControllerTest`, e os antigos ITs agora `*Test`).

## Fatia 1 — RBAC

- PROFESSOR: 403 em PUT escola, POST/PUT aluno, vínculos POST, POST eventos.
- COORDENACAO: PUT escola e POST eventos permitidos.
- Matriz do README alinhada a `@PreAuthorize`.

## Fatia 2 — DTOs

- `TurmaController` devolve `TurmaResponse` / `TurmaAlunoResponse` / `CameraTurmaResponse`.
- Testes em `TurmaControllerTest`.

## Fatia 3 — CI

- Classes `*IT` renomeadas para `*Test` (Surefire passa a executá-las).
- Workflow `.github/workflows/ci.yml` (`mvn -B test`, Java 21).
- Badge no README. Verde no GitHub só após push (HITL).

## Fatia 4 — Docs

- Pacote real `domain/` (não `model/`).
- Texto explícito: sem reconhecimento facial / stream no MVP.

## Riscos residuais

- Isolamento por escola/utilizador ainda não existe.
- Testes em H2, produção MySQL.
- Badge CI fica vermelho/ausente até o workflow correr no remoto.
