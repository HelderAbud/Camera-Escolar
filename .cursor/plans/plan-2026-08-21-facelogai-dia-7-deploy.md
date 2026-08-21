# FaceLogAI Dia 7 — Plano de deploy (portfólio)

**Trilha Helder:** Normal  
**Data:** 2026-08-21  
**Estado:** aprovado para **documentar** decisões. Execução na nuvem = **Dia 8** (HITL).

## Objetivo

Fechar a escolha de plataforma, banco, seed e rollback **sem** subir a API hoje.

## Decisões

| Tema | Escolha | Porquê |
|------|---------|--------|
| Plataforma | **Railway** | MySQL gerido nativo; Flyway V1–V5 é MySQL. Render gerido é sobretudo Postgres. |
| Banco | **MySQL 8** (plugin Railway) | Trilha: não migrar para Postgres sem plano extra. |
| Seed admin | **OFF** em produção | `FACELOGAI_SEED_ADMIN_ENABLED` ausente ou `false`. Admin criado **à mão** no Dia 9. |
| Custo | **Não é R$ 0 permanente** | Railway: crédito/hobby. Render free dorme (~15 min) e Postgres free expira ~30 dias. Documentar no README no Dia 8. |

**Alternativa rejeitada (por agora):** Render + Postgres — exigiria novas migrations e divergiria do Compose local (`mysql:8.0`, porta 3307).

**Alternativa de contingência:** se o trial Railway acabar, Render **Web Service** + MySQL externo (mesmo `jdbc:mysql://…`) — sem mudar dialecto.

## Fora de escopo (Dia 7)

- Dockerfile, `server.port=${PORT:8082}`, deploy real, URL no README (Dia 8).
- Criar utilizador admin em prod (Dia 9).
- Fechar Swagger em prod (Dia 10 — decisão HITL).
- Visão computacional, frontend, isolamento multi-escola.

## Variáveis de ambiente (produção)

Nunca commitar valores reais. Nomes alinhados a [`.env.example`](../../.env.example) e `application.properties`.

| Variável | Obrigatória | Produção |
|----------|-------------|----------|
| `JWT_SECRET_BASE64` | sim | `openssl rand -base64 32`; só no painel |
| `DB_URL` | sim | JDBC Railway, SSL se o host exigir (`useSSL=true`) |
| `DB_USER` | sim | user do plugin MySQL |
| `DB_PASSWORD` | sim | senha do plugin |
| `FACELOGAI_SEED_ADMIN_ENABLED` | sim (explícito) | `false` |
| `FACELOGAI_SEED_ADMIN_PASSWORD` | não | **não definir** |
| `SERVER_PORT` / `PORT` | plataforma | Dia 8: a app deve honrar `PORT` do PaaS |

Health esperado: `GET /health`. Login: `POST /api/auth/login` (JWT). Resto da API autenticado.

## Rollback

1. **Deploy mau:** no Railway, Rollback para o deploy anterior (imagem/commit).
2. **Env errado:** repor o dashboard para o conjunto da tabela acima; **não** ligar seed.
3. **Schema:** Flyway só avança; **nunca** editar `V1`–`V5` já aplicadas. Correção = `V6__…` depois de plano.
4. **Segredo vazou:** gerar novo `JWT_SECRET_BASE64`, invalidar sessões (JWT sem refresh: tokens antigos valem até 1 h).
5. **Voltar a “só local”:** README fica “Demo: em breve”; Compose + `mvn spring-boot:run` na 8082.

## Passos do Dia 8 (não executar agora)

1. Dockerfile multi-stage (Java 21, `mvn -DskipTests package`, JRE).
2. Bind `server.port` a `PORT`.
3. Serviço Web + MySQL no Railway; health `/health`.
4. Env da tabela; seed **false**.
5. HITL: tu colas os secrets no painel (agente não cria conta nem cola senha).
6. README: URL Swagger + aviso de cold start / crédito.

## Validação deste dia

- CI `master` verde (run Actions após merge PR #6).
- Este plano + [`docs/deploy.md`](../../docs/deploy.md) com env e rollback.
- Trilha Dia 7 marcada.

## Critério de pronto (Dia 7)

Escolha Railway + MySQL registada; seed OFF obrigatório; rollback escrito; **nenhum secret no Git**.
