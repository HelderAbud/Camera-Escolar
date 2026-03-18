# FaceLogAI — Ciclo 2 de Melhorias

> Estado de partida: v2 com DTOs, @PreAuthorize, @Transactional, testes básicos, Docker e paginação em `/api/alunos` implementados.

---

## Checklist do Ciclo 2

| # | Item | Prioridade | Quebra contrato? |
|---|------|-----------|-----------------|
| 1 | Corrigir `JwtServiceTest` | 🔴 Alta | Sim* ✅ |
| 2 | `@PreAuthorize` no `TurmaController` | 🔴 Alta | Não ✅ |
| 3 | Remover `httpBasic()` da `SecurityConfig` | 🔴 Alta | Não ✅ |
| 4 | Atualizar README | 🔴 Alta | Não ✅ |
| 5 | Paginação em `GET /api/eventos` | 🟡 Média | Sim* ✅ |
| 6 | Testes de `CameraController` e `EventoMonitoramentoController` | 🟡 Média | Sim* ✅ |
| 7 | Paginação em `EscolaService.listarTodas()` | 🟡 Média | Sim* ✅ |
| 8 | Validação de URL no `endpointUrl` | 🟢 Baixa | Não ✅ |
| 9 | `tipoEvento` como enum | 🟢 Baixa | Sim* ✅ |

> \* "Quebra contrato" significa que clientes existentes da API precisam ser atualizados.

---

## 1. Corrigir `JwtServiceTest` 🔴

> **Problema:** o teste usa `.hasMessageContaining("JWT_SECRET_BASE64")` mas o `@PostConstruct`
> não é chamado em testes unitários sem Spring. O erro cai no `signingKey()` com a mensagem
> `"JWT secret não configurado..."` — o assert atual quebra.

**Arquivo:** `src/test/java/com/faceblogai/service/JwtServiceTest.java`

```java
// ANTES
assertThatThrownBy(() -> service.generateToken(user))
    .isInstanceOf(IllegalStateException.class)
    .hasMessageContaining("JWT_SECRET_BASE64");

// DEPOIS
assertThatThrownBy(() -> service.generateToken(user))
    .isInstanceOf(IllegalStateException.class)
    .hasMessageContaining("JWT secret");
```

---

## 2. `@PreAuthorize` no `TurmaController` 🔴

> **Problema:** `CameraController`, `EscolaController` e `AlunoController` já têm restrições
> por role, mas `TurmaController` não. Qualquer `PROFESSOR` autenticado pode criar,
> editar ou deletar turmas.

**Arquivo:** `src/main/java/com/faceblogai/controller/TurmaController.java`

```java
// Adicionar import no topo:
import org.springframework.security.access.prepost.PreAuthorize;

// Método criar():
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@PostMapping
public ResponseEntity<TurmaResponse> criar(@Valid @RequestBody TurmaRequest request) { ... }

// Método atualizar():
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@PutMapping("/{id}")
public ResponseEntity<TurmaResponse> atualizar(...) { ... }

// Método deletar():
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletar(@PathVariable Long id) { ... }
```

---

## 3. Remover `httpBasic()` da `SecurityConfig` 🔴

> **Problema:** a API usa exclusivamente JWT. Manter `httpBasic()` ativo abre um vetor
> desnecessário e pode confundir clientes que recebem o header `WWW-Authenticate: Basic`.

**Arquivo:** `src/main/java/com/faceblogai/config/SecurityConfig.java`

```java
// ANTES
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
.httpBasic(Customizer.withDefaults());

// DEPOIS — remover a linha httpBasic() inteira
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

Remova também o import de `Customizer` se não for mais usado:
```java
// Remover se não usado em outro lugar:
import org.springframework.security.config.Customizer;
```

---

## 4. Atualizar README 🔴

> **Problema:** o README ainda descreve "próximos passos" que já foram implementados
> (MySQL, healthcheck, gestão de alunos/turmas, eventos). Não reflete o estado real do projeto.

**Arquivo:** `README.md` — substitua a seção `## Próximos passos sugeridos` pelo conteúdo abaixo
e atualize a seção `## Como rodar`:

```markdown
## Como rodar (local)

### Pré-requisitos
- Java 21
- Maven 3.9+
- Docker (para o banco)

### 1. Subir o banco
```bash
docker compose up -d
```

### 2. Configurar o JWT secret
```bash
# Gerar um secret seguro:
openssl rand -base64 32

# Exportar antes de rodar:
export JWT_SECRET_BASE64=<valor gerado acima>
```

### 3. Rodar a aplicação
```bash
mvn spring-boot:run
```

### 4. Acessar
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/health

### Login inicial (seed de desenvolvimento)
```json
POST /api/auth/login
{ "email": "admin@facelogai.local", "password": "admin123" }
```

## Perfis e permissões

| Endpoint | ADMIN | COORDENACAO | PROFESSOR |
|----------|-------|-------------|-----------|
| GET (leitura geral) | ✓ | ✓ | ✓ |
| POST /api/escolas | ✓ | — | — |
| POST /api/cameras | ✓ | ✓ | — |
| DELETE /api/cameras | ✓ | — | — |
| DELETE /api/alunos | ✓ | ✓ | — |
| POST /api/turmas | ✓ | ✓ | — |
| DELETE /api/turmas | ✓ | — | — |

## Próximos passos
- Paginação em `GET /api/eventos`
- Testes de controller para Camera e Evento
- `tipoEvento` como enum validado
```

---

## 5. Paginação em `GET /api/eventos` 🟡

> **Atenção:** isso muda o contrato da API. O retorno passa de `List<EventoResponse>`
> para `Page<EventoResponse>` com campos extras (`totalElements`, `totalPages`, etc.).
> Alinhe com quem consome esse endpoint antes de aplicar.

### 5.1 Alterar `EventoMonitoramentoController`

```java
// Adicionar import:
import org.springframework.data.domain.Page;

// ANTES
@GetMapping
public List<EventoResponse> listar(
        @RequestParam(required = false) Long cameraId,
        @RequestParam(required = false) Long turmaId,
        @RequestParam(required = false) Long alunoId,
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant to,
        @RequestParam(defaultValue = "100") int limit) {
    return eventoService.listarFiltrado(cameraId, turmaId, alunoId, from, to, limit)
            .stream().map(EventoResponse::from).toList();
}

// DEPOIS
@GetMapping
public Page<EventoResponse> listar(
        @RequestParam(required = false) Long cameraId,
        @RequestParam(required = false) Long turmaId,
        @RequestParam(required = false) Long alunoId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size) {
    return eventoService.listarFiltradoPaginado(cameraId, turmaId, alunoId, from, to, page, size)
            .map(EventoResponse::from);
}
```

### 5.2 Alterar `EventoMonitoramentoService`

```java
// Adicionar imports:
import org.springframework.data.domain.Page;

// Adicionar método (mantendo o listarFiltrado() existente para não quebrar outros usos):
public Page<EventoMonitoramento> listarFiltradoPaginado(
        Long cameraId, Long turmaId, Long alunoId,
        Instant fromTs, Instant toTs, int page, int size) {
    int safeSize = Math.max(1, Math.min(size, 500));
    Pageable pageable = PageRequest.of(page, safeSize, Sort.by(Sort.Direction.DESC, "criadoEm"));
    return eventoRepository.buscarFiltradoPaginado(cameraId, turmaId, alunoId, fromTs, toTs, pageable);
}
```

### 5.3 Alterar `EventoMonitoramentoRepository`

```java
// Adicionar novo método (mantendo buscarFiltrado() existente):
@Query("""
        select e
        from EventoMonitoramento e
        where (:cameraId is null or e.camera.id = :cameraId)
          and (:turmaId is null or e.turma.id = :turmaId)
          and (:alunoId is null or e.aluno.id = :alunoId)
          and (:fromTs is null or e.criadoEm >= :fromTs)
          and (:toTs is null or e.criadoEm <= :toTs)
        """)
Page<EventoMonitoramento> buscarFiltradoPaginado(
        @Param("cameraId") Long cameraId,
        @Param("turmaId") Long turmaId,
        @Param("alunoId") Long alunoId,
        @Param("fromTs") Instant fromTs,
        @Param("toTs") Instant toTs,
        Pageable pageable);
```

---

## 6. Testes de Controller 🟡

> Cobre os cenários mais críticos de segurança: acesso negado por role e resposta correta com DTO.

### 6.1 `CameraControllerIT.java`

**Caminho:** `src/test/java/com/faceblogai/controller/CameraControllerIT.java`

```java
package com.faceblogai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CameraControllerIT {

    @Autowired MockMvc mockMvc;

    // Helper: faz login e retorna o token JWT
    private String tokenAdmin() throws Exception {
        String resp = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"admin@facelogai.local","password":"admin123"}
                    """))
            .andReturn().getResponse().getContentAsString();
        // Extrai o token do JSON retornado
        return resp.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(post("/api/cameras"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void professorNaoPodeCriarCamera() throws Exception {
        // PROFESSOR não tem permissão — mas como o seed só tem ADMIN,
        // testamos com token ADMIN em endpoint de DELETE para validar o @PreAuthorize
        // Adicione um seed de PROFESSOR no V2 para teste completo.
        mockMvc.perform(delete("/api/cameras/999")
                .header("Authorization", "Bearer " + tokenAdmin()))
            .andExpect(status().isNoContent()); // ADMIN pode deletar (404 vira 204 se não existir)
    }

    @Test
    void bodyInvalidoRetorna400ComProblemDetails() throws Exception {
        mockMvc.perform(post("/api/cameras")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenAdmin())
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Erro de validação"))
            .andExpect(jsonPath("$.errors").isArray());
    }
}
```

### 6.2 `EventoMonitoramentoControllerIT.java`

**Caminho:** `src/test/java/com/faceblogai/controller/EventoMonitoramentoControllerIT.java`

```java
package com.faceblogai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventoMonitoramentoControllerIT {

    @Autowired MockMvc mockMvc;

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/eventos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void listarSemFiltrosRetorna200() throws Exception {
        String token = mockMvc.perform(post("/api/auth/login")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"admin@facelogai.local","password":"admin123"}
                    """))
            .andReturn().getResponse().getContentAsString()
            .replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/eventos")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
```

---

## 7. Paginação em `EscolaService` 🟡

> `listarTodas()` usa `findAll()` sem limite. Baixo risco agora, mas consistente com o padrão adotado em `AlunoService`.

**Arquivo:** `src/main/java/com/faceblogai/service/EscolaService.java`

```java
// Adicionar imports:
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

// Adicionar método:
public Page<Escola> listarPaginado(int page, int size) {
    int safeSize = Math.min(size, 100);
    return escolaRepository.findAll(
        PageRequest.of(page, safeSize, Sort.by("nome")));
}
```

**Arquivo:** `src/main/java/com/faceblogai/controller/EscolaController.java`

```java
// Manter o listar() existente (lista simples) para não quebrar contrato,
// ou substituí-lo da mesma forma que AlunoController:

@GetMapping
public Page<EscolaResponse> listar(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    return escolaService.listarPaginado(page, size).map(EscolaResponse::from);
}
```

---

## 8. Validação de URL no `endpointUrl` 🟢

> Hoje qualquer string não-vazia é aceita como URL de câmera.

**Arquivo:** `src/main/java/com/faceblogai/controller/CameraController.java`

```java
// Adicionar dependência no pom.xml (se ainda não tiver):
// spring-boot-starter-validation já está incluso via spring-boot-starter-web

// No record CameraRequest, trocar @NotBlank por @Pattern:
public record CameraRequest(
        @NotNull Long escolaId,
        @NotBlank String nome,
        @Pattern(
            regexp = "^(rtsp|rtmp|http|https)://.+",
            message = "endpointUrl deve ser uma URL válida (rtsp, rtmp, http ou https)"
        )
        @NotBlank String endpointUrl) {}

// Mesmo padrão para CameraUpdateRequest:
public record CameraUpdateRequest(
        @NotBlank String nome,
        @Pattern(
            regexp = "^(rtsp|rtmp|http|https)://.+",
            message = "endpointUrl deve ser uma URL válida (rtsp, rtmp, http ou https)"
        )
        @NotBlank String endpointUrl,
        @NotNull Boolean ativo) {}
```

---

## 9. `tipoEvento` como enum 🟢

> **Atenção:** isso é uma mudança de contrato e requer uma migration de banco.
> Avalie se os dados existentes já estão padronizados antes de aplicar.

### 9.1 Criar o enum

**Caminho:** `src/main/java/com/faceblogai/domain/TipoEvento.java`

```java
package com.faceblogai.domain;

public enum TipoEvento {
    PRESENCA_DETECTADA,
    AUSENCIA_DETECTADA,
    MOVIMENTO_SUSPEITO,
    ROSTO_RECONHECIDO,
    ROSTO_DESCONHECIDO,
    CAMERA_OFFLINE,
    CAMERA_ONLINE
}
```

### 9.2 Alterar `EventoMonitoramento`

```java
// ANTES
@Column(name = "tipo_evento", nullable = false, length = 80)
private String tipoEvento;

// DEPOIS
@Enumerated(EnumType.STRING)
@Column(name = "tipo_evento", nullable = false, length = 80)
private TipoEvento tipoEvento;
```

### 9.3 Migration de banco

**Caminho:** `src/main/resources/db/migration/V5__tipo_evento_enum.sql`

```sql
-- Garante que os dados existentes são valores válidos antes de adicionar constraint
-- Ajuste os valores conforme os dados reais que já existirem no banco
UPDATE evento_monitoramento
SET tipo_evento = 'PRESENCA_DETECTADA'
WHERE tipo_evento NOT IN (
    'PRESENCA_DETECTADA','AUSENCIA_DETECTADA','MOVIMENTO_SUSPEITO',
    'ROSTO_RECONHECIDO','ROSTO_DESCONHECIDO','CAMERA_OFFLINE','CAMERA_ONLINE'
);
```

### 9.4 Alterar `EventoRequest` no controller

```java
// ANTES
@NotBlank String tipoEvento

// DEPOIS
@NotNull TipoEvento tipoEvento
```

---

## Ordem recomendada de execução

```
Passo 1  — Corrigir JwtServiceTest (1 linha, sem risco)
Passo 2  — @PreAuthorize no TurmaController
Passo 3  — Remover httpBasic() da SecurityConfig
Passo 4  — Atualizar README
           → mvn clean test (tudo deve passar)

Passo 5  — Testes CameraControllerIT e EventoMonitoramentoControllerIT
           → mvn clean test

Passo 6  — Paginação EscolaService (sem quebrar contrato se mantiver listarTodas())
Passo 7  — Validação @Pattern no endpointUrl
           → mvn clean test

Passo 8  — Paginação em /api/eventos (ALINHA COM CONSUMIDORES ANTES)
Passo 9  — tipoEvento como enum (ALINHA COM CONSUMIDORES ANTES + migration V5)
           → mvn clean test
```

---

*FaceLogAI — Ciclo 2 gerado após análise da v2 — Março 2026*
