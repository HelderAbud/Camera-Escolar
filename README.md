# FaceLogAI — Sistema de apoio a monitoramento escolar

API backend para **gestão de contexto escolar** ligada a câmeras e controle de acesso a dados (escolas, turmas, alunos, câmeras), com autenticação por perfil. Posicionamento: **segurança e organização institucional**, com base para evolução (eventos, presença, visão computacional — ver roadmap).

**Repositório público:** https://github.com/HelderAbud/Camera-Escolar

---

## Resumo para LinkedIn / vitrine (copiar)

**GitHub:** https://github.com/HelderAbud/Camera-Escolar

**Sistema de Monitoramento Escolar (FaceLogAI)**

Projeto com foco em monitoramento e controle de ambiente escolar, com potencial para aplicação em segurança e gestão de presença.

**Tecnologias:** Java 21, Spring Boot 3, Spring Web, Spring Security, Spring Data JPA, Flyway (migrações de banco), JWT (autenticação), Springdoc OpenAPI (Swagger UI)  

**Destaques:**

- Aplicação voltada para cenário real
- Base para evolução com automações e inteligência
- API REST com autenticação por perfis (ex.: ADMIN, COORDENAÇÃO, PROFESSOR)

---

## Visão rápida (portfólio / recrutador)

| Item | Valor |
|------|--------|
| **Problema que resolve** | Centralizar dados escolares (escolas, turmas, alunos, câmeras) com acesso por perfil. |
| **Demo / deploy** | Demo: em breve — ver Dia 8 da [`TRILHA-DIA-A-DIA.md`](TRILHA-DIA-A-DIA.md). Local: [Swagger UI](http://localhost:8080/swagger-ui.html) |
| **Repositório** | `https://github.com/HelderAbud/Camera-Escolar` |

---

## Stack principal

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3 |
| API | Spring Web (REST), Springdoc OpenAPI (Swagger UI) |
| Segurança | Spring Security, JWT |
| Persistência | Spring Data JPA, Flyway |
| Build | Maven |

**Bibliotecas ou integrações adicionais:** ver `pom.xml` (ex.: actuator, logstash encoder); complete aqui se adicionares integrações novas.

---

## Arquitetura

Padrão em camadas: **Controller → Service → Repository**, entidades em `model/`, configuração em `config/`.

```text
src/main/java/com/faceblogai/
├── controller/
├── service/
├── model/
├── repository/
└── config/
```

Diagrama ou print da arquitetura (opcional): coloque em `docs/` e link aqui.

---

## Funcionalidades (estado atual)

- Autenticação JWT e perfis (ex.: ADMIN, COORDENAÇÃO, PROFESSOR) com matriz de permissões documentada abaixo.
- Endpoints de domínio escolar: escolas, câmeras, turmas, alunos (conforme implementado no backend).
- Documentação interativa: Swagger UI.

### Matriz de permissões (resumo)

Perfis no JWT: `ADMIN`, `COORDENACAO`, `PROFESSOR` (enum `PerfilUsuario`; no texto pode aparecer “COORDENAÇÃO”).

| Endpoint | ADMIN | COORDENACAO | PROFESSOR |
|----------|-------|-------------|-----------|
| GET (leitura geral, autenticado) | ✓ | ✓ | ✓ |
| POST /api/escolas | ✓ | — | — |
| POST /api/cameras | ✓ | ✓ | — |
| DELETE /api/cameras | ✓ | — | — |
| DELETE /api/alunos | ✓ | ✓ | — |
| POST /api/turmas | ✓ | ✓ | — |
| DELETE /api/turmas | ✓ | — | — |

---

## Como rodar (local)

### Pré-requisitos

- Java 21
- Maven 3.9+
- Docker (para o banco)

### Passos

```bash
cp .env.example .env
# Edite o .env com senhas locais antes de subir o banco.
```

```bash
docker compose up -d
```

```bash
# Gerar secret seguro:
openssl rand -base64 32
export JWT_SECRET_BASE64=[VALOR_GERADO]

# Opcional para ambiente local/dev: criar admin inicial em runtime
export FACELOGAI_SEED_ADMIN_ENABLED=true
export FACELOGAI_SEED_ADMIN_EMAIL=admin@facelogai.local
export FACELOGAI_SEED_ADMIN_PASSWORD=[SENHA_LOCAL_FORTE]
export DB_PASSWORD=[SENHA_MYSQL_LOCAL]
```

```bash
mvn spring-boot:run
```

- **API:** http://localhost:8080  
- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **Health:** http://localhost:8080/health  

### Login de desenvolvimento

`POST /api/auth/login`

```json
{ "email": "admin@facelogai.local", "password": "[SENHA_LOCAL_FORTE]" }
```

O seed admin fica desabilitado por padrão. Habilite apenas em ambiente local/dev com `FACELOGAI_SEED_ADMIN_ENABLED=true` e defina a senha por variável de ambiente. Não use senha padrão em deploy.

---

## Documentação adicional

- [docs/BACKLOG.md](docs/BACKLOG.md) — marcos **B0–B2 concluídos** ✅
- [docs/WORKFLOW.md](docs/WORKFLOW.md)
- [TRILHA-DIA-A-DIA.md](TRILHA-DIA-A-DIA.md) — trilha portfólio (Helder + skills-pessoal)

---

## Screenshots (opcional)

| Tela / Swagger | Ficheiro sugerido |
|----------------|-------------------|
| Swagger UI | `docs/screenshots/swagger.png` |

---

## Roadmap (ideias alinhadas ao mercado)

- [ ] Notificações de eventos
- [ ] Registo de presença integrado ao fluxo escolar
- [ ] Integração com IA / visão computacional (quando for objetivo do projeto)
- [ ] Deploy (ex.: Render, Fly.io) — adicionar URL aqui e no topo quando existir

---

## Licença

Definir no repositório (ex.: MIT) ou remover esta secção se não aplicável.
