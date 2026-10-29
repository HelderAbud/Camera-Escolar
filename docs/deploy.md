# Deploy FaceLogAI (portfólio)

Decisões: [`.cursor/plans/plan-2026-08-21-facelogai-dia-7-deploy.md`](../.cursor/plans/plan-2026-08-21-facelogai-dia-7-deploy.md).  
Preparação da imagem: [`.cursor/plans/plan-2026-10-23-facelogai-dia-8-deploy.md`](../.cursor/plans/plan-2026-10-23-facelogai-dia-8-deploy.md).  
**Estado:** `Dockerfile` e `PORT` no repositório. URL pública ainda não — conta Railway e secrets são HITL.

## Alvo

- **PaaS:** Railway (Web + MySQL 8).
- **Não:** seed admin automático em produção.
- **Local continua:** `docker compose up -d` (MySQL `:3307`) + `mvn spring-boot:run` (`:8082`).

## Variáveis (painel, nunca Git)

```text
JWT_SECRET_BASE64=<openssl rand -base64 32>
DB_URL=jdbc:mysql://<host>:3306/<db>?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=<user>
DB_PASSWORD=<password>
FACELOGAI_SEED_ADMIN_ENABLED=false
```

Não definir `FACELOGAI_SEED_ADMIN_PASSWORD` em produção.

A API escuta `PORT` (padrão local `8082`): `server.port=${PORT:8082}`.

## Imagem

`Dockerfile` multi-stage (Java 21). Não copia `.env`.

Neste PC (Windows) não há Docker. O build da imagem fica no Railway ou no Ubuntu (WSL):

```bash
docker build -t facelogai .
```

Health esperado dentro do contentor: `GET /health`.

## O que o Helder faz no Railway

1. Novo projeto: serviço a partir deste repositório (builder Dockerfile) + plugin MySQL 8.
2. Colar as variáveis da lista acima no painel. Não definir `FACELOGAI_SEED_ADMIN_PASSWORD`.
3. Health check: `/health`.
4. Quando `GET /health` responder na URL pública, atualizar o README com o Swagger (`/swagger-ui.html`).

Railway usa crédito/hobby. Não é R$ 0 permanente. Se o trial acabar, a contingência é um web service com MySQL externo no mesmo JDBC, sem mudar o dialeto.

## Rollback rápido

| Problema | Ação |
|----------|------|
| App quebrada após deploy | Rollback do serviço para o commit anterior |
| Ligação à BD | Conferir `DB_URL` / user / SSL; não alterar migrations antigas |
| JWT inválido | Novo `JWT_SECRET_BASE64`; logins de novo |
| Seed ligado por engano | `FACELOGAI_SEED_ADMIN_ENABLED=false` + redeploy; tratar senha como comprometida se esteve no env |
| Queres só local | Remover URL do README; usar Compose |

## HITL

Conta Railway, cartão/crédito e colar secrets: **só o Helder**. Agente não faz login no PaaS nem lê `.env`.
