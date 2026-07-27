# Validation — Trilha Dia 2 (screenshots)

**Data:** 2026-07-27  
**Branch:** working tree local (aguardando HITL para commit/PR)  
**Triagem:** Helder Simple · fast path (docs/portfólio)  
**API local:** H2 via `docs/screenshots/_pw/run-api.cmd` (test classpath; sem MySQL)

## Checklist Dia 2 (TRILHA-DIA-A-DIA.md)

| Item | Resultado |
|------|-----------|
| `docs/screenshots/swagger.png` | OK — Swagger UI local (`/swagger-ui.html`) |
| `docs/screenshots/eventos-filtro.png` | OK — GET `/api/eventos?cameraId=1&page=0&size=10` autenticado → HTTP 200 |
| `docs/screenshots/permissions-matrix.png` | OK — matriz alinhada ao README |
| README secção Screenshots preenchida | OK — 3 imagens linkadas |

## Ficheiros desta fatia

- `docs/screenshots/swagger.png`
- `docs/screenshots/eventos-filtro.png`
- `docs/screenshots/permissions-matrix.png`
- `README.md`
- `TRILHA-DIA-A-DIA.md`
- `.gitignore` (helpers `_pw` locais)
- este grill-log
- `.cursor/plans/plan-2026-07-27-facelogai-dia-2-screenshots.md`

## Notas

- Captura com Chrome headless (Playwright npm bloqueado por certificado SSL no ambiente).
- Seed admin só em memória H2 local (`TestOnly-…`); não usar em deploy.
- `pom.xml` ainda sem `mysql-connector` em runtime — fora do escopo do Dia 2.

## Aprovado?

- [x] Fatia Dia 2 verificável (3 PNGs + README + trilha)
- [ ] Commit/push/PR — aguarda confirmação HITL
