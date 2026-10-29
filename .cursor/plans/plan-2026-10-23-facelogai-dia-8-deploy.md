# FaceLogAI Dia 8 — Imagem e porta do PaaS

**Trilha Helder:** Normal  
**Data:** 2026-10-23  
**Estado:** preparação no repositório feita. Deploy real continua HITL.

## Problem

A API escuta só a porta 8082. No Railway o processo precisa da variável `PORT`, e ainda não existe imagem para publicar.

## Scope

- `server.port=${PORT:8082}` (local continua 8082).
- Dockerfile multi-stage Java 21 e `.dockerignore`.
- Teste que prova o placeholder.
- `docs/deploy.md`, README e trilha: imagem pronta; URL pública ainda não.

## Out of scope

- Criar conta Railway, colar secrets, ler `.env`.
- URL de Swagger no README (só depois do health público).
- Seed admin, fechar Swagger, Postgres, frontend.

## Acceptance

- Sem `PORT`, a configuração resolve 8082.
- Com `PORT=4321`, resolve 4321.
- `mvn clean test` verde.
- Dockerfile não copia `.env`.

## Risks

- Build da imagem não roda neste Windows (sem Docker). Conferir no Railway ou no Ubuntu.
- Railway não é grátis para sempre.
