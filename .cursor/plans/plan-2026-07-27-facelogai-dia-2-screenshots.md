# Spec — FaceLogAI Dia 2 (screenshots)

**Status:** ready-for-execution  
**Trilha:** Simple (docs/portfólio) · captura precisa API local  
**Data:** 2026-07-27

## Problem

README ainda lista Screenshots incompletos; trilha Dia 2 exige 3 PNGs e secção preenchida.

## Scope

- `docs/screenshots/swagger.png`
- `docs/screenshots/eventos-filtro.png` (GET `/api/eventos` com filtros no Swagger)
- `docs/screenshots/permissions-matrix.png` (matriz do README)
- Atualizar README secção Screenshots
- Marcar Dia 2 em `TRILHA-DIA-A-DIA.md` + grill-log

## Out of scope

- Commit/push/PR (HITL)
- Deploy, CI, Dia 3 etapas
- Alterar matriz de permissões / código de segurança

## Acceptance criteria

- [ ] 3 PNGs existem em `docs/screenshots/`
- [ ] README liga os 3 ficheiros
- [ ] Checkbox Dia 2 fechado na trilha
- [ ] Grill-log de validação

## Risks

- Docker só via WSL; `.env` local (não commit)
- Seed admin só local com `FACELOGAI_SEED_ADMIN_ENABLED=true`

## Verification

- Ficheiros PNG presentes
- Links README resolvem
- Checklist Dia 2 marcado
