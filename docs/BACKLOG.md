# BACKLOG — FaceLogAI (MVP)

> Ordem sugerida para acelerar: **B0 → B1 → B2**

Marque [x] quando concluir.  
Sempre em incrementos pequenos, testáveis.

---

## Marco B0 — Setup e fundação
- [x] **B0-01** Criar projeto Spring Boot base + healthcheck  
      - Projeto Maven criado (`pom.xml`) com Spring Boot 3, Web, Security, JPA, Actuator e Swagger.  
      - Classe `FaceLogAIApplication` criada e endpoint `/health` respondendo `status: UP`.  
      - `mvn clean test` executado com sucesso.
- [x] **B0-02** Configurar banco (H2 dev) + JPA  
      - Optamos por ir direto para **MySQL + JPA**, seguindo o padrão dos outros projetos.  
      - `application.properties` configurado com datasource `jdbc:mysql://localhost:3306/facelogai` e usuário `facelogai`.  
      - Entidade `Usuario` mapeada para a tabela `usuario`.
- [x] **B0-03** Flyway habilitado + primeira migration  
      - Flyway habilitado em `application.properties`.  
      - Criada migration `V1__create_base_tables.sql` com tabelas `usuario`, `escola` e `camera`.
- [x] **B0-04** Swagger (OpenAPI) ligado  
      - Dependência `springdoc-openapi-starter-webmvc-ui` adicionada ao `pom.xml`.  
      - Endpoints padrão do Springdoc expostos em `/v3/api-docs` e `/swagger-ui.html`.
- [x] **B0-05** Qualidade mínima (tests + format)  
      - Projeto compila e `mvn clean test` roda sem falhas após cada incremento.  
      - Suite básica de testes criada (unitários de auth/JWT e integração de login).

## Marco B1 — Usuários e autenticação
- [x] **B1-01** Cadastro/Login com JWT  
      - Entidade `Usuario`, `UsuarioRepository`, `AuthService`, `JwtService` e `AuthController` criados.  
      - Endpoint `POST /api/auth/login` gerando JWT com email e role, retornando 401 em caso de falha.
- [x] **B1-02** Perfis básicos (ADMIN, COORDENACAO, PROFESSOR)  
      - Enum `PerfilUsuario` criado com esses três perfis.  
      - Campo `role` da entidade `Usuario` mapeado para esse enum e persistido na tabela `usuario`.
- [x] **B1-03** Controle de acesso a endpoints  
      - `SecurityConfig` configurado para liberar `/health`, Swagger e `/api/auth/login` e exigir autenticação no restante.  
      - `JwtAuthenticationFilter` criado para popular o contexto de segurança a partir do token JWT.

## Marco B2 — Domínio de câmeras e eventos
- [x] **B2-01** CRUD de câmeras vinculadas à escola  
      - Entidades JPA `Escola` e `Camera` criadas e mapeadas para as tabelas existentes.  
      - Repositórios `EscolaRepository` e `CameraRepository` com consulta por escola.  
      - Services `EscolaService` e `CameraService` encapsulando a lógica de CRUD.  
      - Controllers REST `EscolaController` e `CameraController` com endpoints em `/api/escolas` e `/api/cameras`.
- [x] **B2-02** Entidades de aluno, turma e vínculo com câmeras  
      - Criadas entidades JPA `Aluno`, `Turma`, `TurmaAluno` e `CameraTurma` com migrations (`V3__aluno_turma_vinculos.sql`).  
      - Repositórios `AlunoRepository`, `TurmaRepository`, `TurmaAlunoRepository` e `CameraTurmaRepository`.  
      - Services `AlunoService`, `TurmaService` e `VinculoService` para CRUD e vínculos aluno-turma e câmera-turma.  
      - Controllers `AlunoController` e `TurmaController` com endpoints REST para gerenciar alunos, turmas e vínculos.  
      - `GET /api/alunos` agora retorna listagem paginada (`page`, `size`) ao invés de lista simples.
- [x] **B2-03** Registro de eventos de monitoramento (logs)  
      - Migration `V4__evento_monitoramento.sql` criada com tabela `evento_monitoramento` (FK para `camera`, e `turma`/`aluno` opcionais).  
      - Entidade JPA `EventoMonitoramento` + `EventoMonitoramentoRepository` e `EventoMonitoramentoService`.  
      - Endpoints em `EventoMonitoramentoController`:  
        - `POST /api/eventos` para registrar um evento  
        - `GET /api/eventos` (últimos 100) e `GET /api/eventos/{id}`
- [x] **B2-04** Listagem filtrada de eventos (por turma, aluno, período)  
      - `GET /api/eventos` agora aceita filtros: `cameraId`, `turmaId`, `alunoId`, `from`, `to` e `limit` (padrão 100; máx 500).  
      - Consulta ordena por `criadoEm desc` e retorna uma resposta paginada (com `content` + metadados) já filtrada.

## Marco P1 — Polimento profissional
- [x] **P1-01** Padronizar erros (Problem Details)  
      - Criado `ApiExceptionHandler` (`@RestControllerAdvice`) retornando RFC7807 (`ProblemDetail`).  
      - Padronizados: `IllegalArgumentException` → 400, validação (`MethodArgumentNotValidException`) → 400 com lista de campos em `errors`, e erro genérico → 500.  
      - Adicionado `timestamp` e `instance` (path) no payload.
- [x] **P1-02** Observabilidade mínima (logs estruturados)  
      - Logs em **JSON** via `logback-spring.xml` + `logstash-logback-encoder`.  
      - Filtro `RequestLoggingFilter` adiciona/propaga `X-Request-Id`, registra `request.completed` com `method`, `path`, `status` e `durationMs` via MDC.  
      - `ApiExceptionHandler` loga validação (WARN), argumentos inválidos (WARN) e exceções não tratadas (ERROR), mantendo correlação por `requestId`.
- [x] **P1-03** Documentação final (README de produto)  
      - README atualizado com visão de produto: objetivo, stack, arquitetura, principais endpoints, autenticação, erros (Problem Details), logs estruturados e estado do MVP.  
      - Instruções claras de configuração local (MySQL, variáveis de ambiente, comando de execução).

