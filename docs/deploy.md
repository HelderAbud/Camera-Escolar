# Deploy FaceLogAI (portfólio)

Decisões: [`.cursor/plans/plan-2026-08-21-facelogai-dia-7-deploy.md`](../.cursor/plans/plan-2026-08-21-facelogai-dia-7-deploy.md).  
**Estado:** plano (Dia 7). URL pública = Dia 8.

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

No Dia 8 a API deve escutar a porta que o PaaS injeta (`PORT`).

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
