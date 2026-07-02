# FaceLogAI — Ciclo 3 de Melhorias

> Estado de partida: v3 com TipoEvento enum, paginação em todos os endpoints,
> testes com @Sql fixture, SecurityConfig limpa e README atualizado.

---

## O que foi resolvido na v3 ✓

Todos os itens do Ciclo 2 estão implementados:
`JwtServiceTest` corrigido, `@PreAuthorize` no `TurmaController`, `httpBasic()` removido,
`HttpStatusEntryPoint` para 401 limpo, `TipoEvento` como enum + migration V5,
paginação em `/api/eventos` e `/api/escolas`, `CameraControllerIT` e
`EventoMonitoramentoControllerIT` com `@Sql`, README atualizado.

---

## Checklist do Ciclo 3

| # | Item | Prioridade | Quebra contrato? |
|---|------|-----------|-----------------|
| 1 | DTO para `TurmaController` (retornos de `Turma`) | 🔴 Alta | Não |
| 2 | DTOs para vínculos (`TurmaAluno`, `CameraTurma`) | 🔴 Alta | Não |
| 3 | `@PreAuthorize` nos endpoints de vínculo | 🔴 Alta | Não |
| 4 | `@Transactional` no `EventoMonitoramentoService.registrar()` | 🔴 Alta | Não |
| 5 | Validação `@Pattern` no `endpointUrl` da câmera | 🟡 Média | Não |
| 6 | Testes para `TurmaController` | 🟡 Média | Não |
| 7 | Seed de `PROFESSOR` nos fixtures de teste | 🟡 Média | Não |
| 8 | Remover `listarFiltrado()` (método List legado) | 🟢 Baixa | Não |

---

## 1. DTO para `TurmaController` 🔴

> **Problema:** `TurmaController` é o único controller que ainda retorna entidades JPA
> diretamente (`Turma`, `TurmaAluno`, `CameraTurma`). Isso expõe estrutura interna e
> serializa relacionamentos lazy de forma imprevisível.

### 1.1 Criar `TurmaResponse.java`

**Caminho:** `src/main/java/com/faceblogai/dto/TurmaResponse.java`

```java
package com.faceblogai.dto;

import com.faceblogai.domain.Turma;
import java.time.Instant;

public record TurmaResponse(
        Long id,
        Long escolaId,
        String escolaNome,
        String nome,
        String serie,
        Instant criadoEm) {

    public static TurmaResponse from(Turma turma) {
        return new TurmaResponse(
                turma.getId(),
                turma.getEscola().getId(),
                turma.getEscola().getNome(),
                turma.getNome(),
                turma.getSerie(),
                turma.getCriadoEm());
    }
}
```

### 1.2 Atualizar `TurmaController`

```java
// Adicionar import:
import com.faceblogai.dto.TurmaResponse;

// listarPorEscola():
@GetMapping("/escola/{escolaId}")
public List<TurmaResponse> listarPorEscola(@PathVariable Long escolaId) {
    return turmaService.listarPorEscola(escolaId)
            .stream().map(TurmaResponse::from).toList();
}

// buscarPorId():
@GetMapping("/{id}")
public ResponseEntity<TurmaResponse> buscarPorId(@PathVariable Long id) {
    return turmaService.buscarPorId(id)
            .map(TurmaResponse::from)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
}

// criar():
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@PostMapping
public ResponseEntity<TurmaResponse> criar(@Valid @RequestBody TurmaRequest request) {
    Turma turma = turmaService.criar(request.escolaId(), request.nome(), request.serie());
    return ResponseEntity.created(URI.create("/api/turmas/" + turma.getId()))
            .body(TurmaResponse.from(turma));
}

// atualizar():
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@PutMapping("/{id}")
public ResponseEntity<TurmaResponse> atualizar(
        @PathVariable Long id, @Valid @RequestBody TurmaUpdateRequest request) {
    return turmaService.atualizar(id, request.nome(), request.serie())
            .map(TurmaResponse::from)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

---

## 2. DTOs para Vínculos 🔴

> **Problema:** os endpoints de vínculo (`/api/turmas/{id}/alunos`, `/api/turmas/{id}/cameras`)
> retornam as entidades `TurmaAluno` e `CameraTurma` diretamente. Essas entidades têm
> relacionamentos bidirecionais que podem causar loops de serialização JSON.

### 2.1 Criar `TurmaAlunoResponse.java`

**Caminho:** `src/main/java/com/faceblogai/dto/TurmaAlunoResponse.java`

```java
package com.faceblogai.dto;

import com.faceblogai.domain.TurmaAluno;
import java.time.Instant;

public record TurmaAlunoResponse(
        Long id,
        Long turmaId,
        String turmaNome,
        Long alunoId,
        String alunoNome,
        String alunoMatricula,
        Instant criadoEm) {

    public static TurmaAlunoResponse from(TurmaAluno ta) {
        return new TurmaAlunoResponse(
                ta.getId(),
                ta.getTurma().getId(),
                ta.getTurma().getNome(),
                ta.getAluno().getId(),
                ta.getAluno().getNome(),
                ta.getAluno().getMatricula(),
                ta.getCriadoEm());
    }
}
```

### 2.2 Criar `CameraTurmaResponse.java`

**Caminho:** `src/main/java/com/faceblogai/dto/CameraTurmaResponse.java`

```java
package com.faceblogai.dto;

import com.faceblogai.domain.CameraTurma;
import java.time.Instant;

public record CameraTurmaResponse(
        Long id,
        Long cameraId,
        String cameraNome,
        Long turmaId,
        String turmaNome,
        Instant criadoEm) {

    public static CameraTurmaResponse from(CameraTurma ct) {
        return new CameraTurmaResponse(
                ct.getId(),
                ct.getCamera().getId(),
                ct.getCamera().getNome(),
                ct.getTurma().getId(),
                ct.getTurma().getNome(),
                ct.getCriadoEm());
    }
}
```

### 2.3 Atualizar os endpoints de vínculo no `TurmaController`

```java
// Adicionar imports:
import com.faceblogai.dto.TurmaAlunoResponse;
import com.faceblogai.dto.CameraTurmaResponse;

// vincularAluno():
@PostMapping("/{turmaId}/alunos/{alunoId}")
public TurmaAlunoResponse vincularAluno(
        @PathVariable Long turmaId, @PathVariable Long alunoId) {
    return TurmaAlunoResponse.from(vinculoService.vincularAlunoEmTurma(turmaId, alunoId));
}

// listarAlunosDaTurma():
@GetMapping("/{turmaId}/alunos")
public List<TurmaAlunoResponse> listarAlunosDaTurma(@PathVariable Long turmaId) {
    return vinculoService.listarAlunosDaTurma(turmaId)
            .stream().map(TurmaAlunoResponse::from).toList();
}

// vincularCamera():
@PostMapping("/{turmaId}/cameras/{cameraId}")
public CameraTurmaResponse vincularCamera(
        @PathVariable Long turmaId, @PathVariable Long cameraId) {
    return CameraTurmaResponse.from(vinculoService.vincularCameraEmTurma(turmaId, cameraId));
}

// listarCamerasDaTurma():
@GetMapping("/{turmaId}/cameras")
public List<CameraTurmaResponse> listarCamerasDaTurma(@PathVariable Long turmaId) {
    return vinculoService.listarCamerasDaTurma(turmaId)
            .stream().map(CameraTurmaResponse::from).toList();
}
```

---

## 3. `@PreAuthorize` nos Endpoints de Vínculo 🔴

> **Problema:** qualquer `PROFESSOR` autenticado pode vincular e desvincular alunos e câmeras
> de turmas. Esses endpoints estão sem restrição de role.

**Arquivo:** `TurmaController.java` — adicionar nas operações de escrita dos vínculos:

```java
// Vincular aluno à turma: ADMIN ou COORDENACAO
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@PostMapping("/{turmaId}/alunos/{alunoId}")
public TurmaAlunoResponse vincularAluno(...) { ... }

// Desvincular aluno da turma: ADMIN ou COORDENACAO
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@DeleteMapping("/{turmaId}/alunos/{alunoId}")
public ResponseEntity<Void> desvincularAluno(...) { ... }

// Vincular câmera à turma: ADMIN ou COORDENACAO
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@PostMapping("/{turmaId}/cameras/{cameraId}")
public CameraTurmaResponse vincularCamera(...) { ... }

// Desvincular câmera da turma: ADMIN ou COORDENACAO
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@DeleteMapping("/{turmaId}/cameras/{cameraId}")
public ResponseEntity<Void> desvincularCamera(...) { ... }
```

---

## 4. `@Transactional` no `EventoMonitoramentoService.registrar()` 🔴

> **Problema:** o método `registrar()` faz múltiplas consultas ao banco (câmera, turma, aluno)
> e depois salva o evento. Se o save falhar após as consultas, o estado fica inconsistente.
> É o único método de escrita nos services que ainda não tem `@Transactional`.

**Arquivo:** `src/main/java/com/faceblogai/service/EventoMonitoramentoService.java`

```java
// Adicionar import (já deve existir):
import org.springframework.transaction.annotation.Transactional;

// ANTES
public EventoMonitoramento registrar(
        Long cameraId, Long turmaId, Long alunoId,
        TipoEvento tipoEvento, String detalhes) {

// DEPOIS
@Transactional
public EventoMonitoramento registrar(
        Long cameraId, Long turmaId, Long alunoId,
        TipoEvento tipoEvento, String detalhes) {
```

---

## 5. Validação `@Pattern` no `endpointUrl` 🟡

> **Problema:** o campo `endpointUrl` em `CameraController` aceita qualquer string não-vazia,
> incluindo valores sem sentido como `"abc"` ou `"123"`.

**Arquivo:** `src/main/java/com/faceblogai/controller/CameraController.java`

```java
// Adicionar import:
import jakarta.validation.constraints.Pattern;

// Substituir @NotBlank no endpointUrl de CameraRequest:
public record CameraRequest(
        @NotNull Long escolaId,
        @NotBlank String nome,
        @NotBlank
        @Pattern(
            regexp = "^(rtsp|rtmp|http|https)://.+",
            message = "endpointUrl deve começar com rtsp://, rtmp://, http:// ou https://")
        String endpointUrl) {}

// Mesmo padrão em CameraUpdateRequest:
public record CameraUpdateRequest(
        @NotBlank String nome,
        @NotBlank
        @Pattern(
            regexp = "^(rtsp|rtmp|http|https)://.+",
            message = "endpointUrl deve começar com rtsp://, rtmp://, http:// ou https://")
        String endpointUrl,
        @NotNull Boolean ativo) {}
```

---

## 6. Testes para `TurmaController` 🟡

> Cobre os cenários principais: 201 com DTO, 401 sem token, 403 com role errada,
> e 400 com body inválido.

### 6.1 Criar fixture SQL

**Caminho:** `src/test/resources/sql/turma-test-data.sql`

```sql
INSERT INTO escola (id, nome, criado_em)
VALUES (1, 'Escola SQL Turma', CURRENT_TIMESTAMP);

INSERT INTO turma (id, escola_id, nome, serie, criado_em)
VALUES (1, 1, 'Turma SQL', '1A', CURRENT_TIMESTAMP);
```

### 6.2 Criar `TurmaControllerIT.java`

**Caminho:** `src/test/java/com/faceblogai/controller/TurmaControllerIT.java`

```java
package com.faceblogai.controller;

import com.faceblogai.domain.PerfilUsuario;
import com.faceblogai.domain.Usuario;
import com.faceblogai.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/turma-test-data.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/cleanup.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TurmaControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;

    private String tokenAdmin;
    private String tokenProfessor;

    private static final long ESCOLA_ID = 1L;
    private static final long TURMA_ID  = 1L;

    @BeforeEach
    void setup() {
        tokenAdmin = jwtService.generateToken(
                new Usuario("Admin", "admin@test.local", "hash", PerfilUsuario.ADMIN));
        tokenProfessor = jwtService.generateToken(
                new Usuario("Prof", "prof@test.local", "hash", PerfilUsuario.PROFESSOR));
    }

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(post("/api/turmas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"escolaId\":1,\"nome\":\"Turma X\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void adminPodeCriarTurmaERetornaDto() throws Exception {
        mockMvc.perform(post("/api/turmas")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content("{\"escolaId\":%d,\"nome\":\"Turma Nova\",\"serie\":\"2B\"}"
                        .formatted(ESCOLA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.escolaId").value(ESCOLA_ID))
            .andExpect(jsonPath("$.nome").value("Turma Nova"))
            .andExpect(jsonPath("$.serie").value("2B"));
    }

    @Test
    void professorNaoPodeCriarTurmaRetorna403() throws Exception {
        mockMvc.perform(post("/api/turmas")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenProfessor)
                .content("{\"escolaId\":%d,\"nome\":\"Turma X\"}".formatted(ESCOLA_ID)))
            .andExpect(status().isForbidden());
    }

    @Test
    void listarPorEscolaRetornaDtoComEscolaNome() throws Exception {
        mockMvc.perform(get("/api/turmas/escola/" + ESCOLA_ID)
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(TURMA_ID))
            .andExpect(jsonPath("$[0].escolaNome").value("Escola SQL Turma"))
            .andExpect(jsonPath("$[0].nome").value("Turma SQL"));
    }

    @Test
    void bodyInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/api/turmas")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Erro de validação"));
    }
}
```

---

## 7. Seed de `PROFESSOR` nos Fixtures 🟡

> **Problema:** `CameraControllerIT` gera o token de `PROFESSOR` direto no `JwtService`
> sem um usuário real no banco. Isso funciona para testar 403, mas não testa o fluxo
> completo de login → token → acesso. Adicionar um usuário PROFESSOR no seed de teste
> permite validar o comportamento end-to-end no `AuthControllerIT`.

**Caminho:** `src/test/resources/sql/camera-test-data.sql` — adicionar ao final:

```sql
-- Usuário PROFESSOR para testes de autorização
-- senha definida apenas no ambiente de teste/local
-- Hash BCrypt gerado para a senha de teste configurada:
INSERT INTO usuario (nome, email, senha_hash, role, criado_em)
VALUES (
    'Professor Teste',
    'prof@facelogai.local',
    '<bcrypt-da-senha-de-teste-configurada>',
    'PROFESSOR',
    CURRENT_TIMESTAMP
)
ON DUPLICATE KEY UPDATE email = email;
```

Adicione o mesmo bloco em `evento-test-data.sql` e `turma-test-data.sql`.

---

## 8. Remover `listarFiltrado()` legado 🟢

> **Problema:** `EventoMonitoramentoService` ainda tem `listarFiltrado()` que retorna `List`
> e `listarUltimos100()`, ambos substituídos por `listarFiltradoPaginado()`.
> São métodos mortos que poluem o código.

**Arquivo:** `src/main/java/com/faceblogai/service/EventoMonitoramentoService.java`

```java
// REMOVER estes dois métodos inteiros:

public List<EventoMonitoramento> listarUltimos100() {
    return eventoRepository.findTop100ByOrderByCriadoEmDesc();
}

public List<EventoMonitoramento> listarFiltrado(
        Long cameraId, Long turmaId, Long alunoId,
        Instant fromTs, Instant toTs, int limit) {
    ...
}
```

**Arquivo:** `src/main/java/com/faceblogai/repository/EventoMonitoramentoRepository.java`

```java
// REMOVER também do repository:
List<EventoMonitoramento> findTop100ByOrderByCriadoEmDesc();

// E o método buscarFiltrado() que retorna List<EventoMonitoramento>
```

> ⚠️ Antes de remover, confirme com `Find Usages` no Cursor que nenhum outro
> arquivo ainda chama esses métodos.

---

## Ordem recomendada de execução

```
Passo 1  — @Transactional no EventoMonitoramentoService.registrar()  (1 linha)
Passo 2  — Criar TurmaResponse, TurmaAlunoResponse, CameraTurmaResponse
Passo 3  — Atualizar TurmaController para usar os DTOs
           → mvn clean test

Passo 4  — @PreAuthorize nos endpoints de vínculo
Passo 5  — Criar turma-test-data.sql + TurmaControllerIT
           → mvn clean test

Passo 6  — Validação @Pattern no endpointUrl
Passo 7  — Seed de PROFESSOR nos fixtures
           → mvn clean test

Passo 8  — Remover listarFiltrado() e listarUltimos100() legados
           → mvn clean test (confirmar 0 falhas)
```

---

*FaceLogAI — Ciclo 3 gerado após análise da v3 — Março 2026*
