# Validação — FaceLogAI Dia 8 (imagem e PORT)

**Data:** 2026-10-23  
**Trilha:** Normal  
**Fatia:** Dockerfile + `server.port=${PORT:8082}`  
**Comando:** `mvn -B clean test` (H2; sem Docker neste PC)  
**Resultado:** BUILD SUCCESS — **31** testes, 0 falhas (inclui `PaasPortBindingTest`).

## Comportamento

- Sem `PORT`, o placeholder resolve `8082`.
- Com `PORT=4321`, resolve `4321`.
- `.dockerignore` exclui `.env`.

## Não verificado

- `docker build` (não há Docker no Windows).
- Processo Spring a escutar `PORT` de verdade (o teste lê o properties; o boot no PaaS fica no Railway).
- Health público e URL do Swagger (HITL: conta, MySQL e secrets).

## Riscos

- Railway não é grátis para sempre.
- O nome do jar no `Dockerfile` acompanha `0.0.1-SNAPSHOT`.
