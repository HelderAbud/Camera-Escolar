# Validação — FaceLogAI trilha Dia 7

**Data:** 2026-08-21  
**Trilha:** Normal (plano de deploy; sem secrets)

## CI (pedido do Helder)

- Repo: `HelderAbud/Camera-Escolar`
- `master` push após merge PR #6: workflow **CI** `success` (id `32507766868`, ~49s, 2026-08-21T17:21:51Z)
- PR #6 também `success` (`32506788947`)

## Dia 7

- Plataforma: Railway
- Banco: MySQL 8 (sem Postgres / sem reescrever Flyway)
- Seed prod: OFF
- Artefactos: `.cursor/plans/plan-2026-08-21-facelogai-dia-7-deploy.md`, `docs/deploy.md`

## Não feito (propositado)

- Dockerfile, conta Railway, URL pública (Dia 8)
- Nenhum valor de `.env` lido ou commitado

## Riscos

- Railway não é free eterno; README no Dia 8 deve ser honesto.
- `server.port` fixo em 8082 pode falhar no PaaS até o Dia 8 honrar `PORT`.
