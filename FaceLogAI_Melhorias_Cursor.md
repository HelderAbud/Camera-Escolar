# FaceLogAI — Guia Completo de Melhorias para o Cursor

> Versão MVP — Março 2026

---

## Resumo das Melhorias

| Prioridade | Item |
|------------|------|
| 🔴 Alta | ~~1 — DTOs de resposta (segurança)~~ ✅ |
| 🔴 Alta | ~~2 — `@PreAuthorize` por role nos endpoints~~ ✅ |
| 🔴 Alta | ~~3 — Testes unitários e de integração~~ ✅ |
| 🟡 Média | 4 — Paginação real nas listagens |
| 🟡 Média | 5 — `@Transactional` nos services |
| 🟡 Média | 6 — `@PostConstruct` para JWT secret |
| 🟢 Baixa | 7 — Docker Compose + perfil de teste H2 |

---

## 1. DTOs de Resposta 🔴 Alta Prioridade

> **Problema:** Os controllers retornam entidades JPA diretamente. Isso acopla o contrato da API ao banco de dados e pode vazar dados sensíveis. Qualquer mudança no modelo quebra a API automaticamente.

### 1.1 Criar o pacote `dto`

Crie a pasta: `src/main/java/com/faceblogai/dto/`

---

#### `CameraResponse.java`
**Caminho:** `src/main/java/com/faceblogai/dto/CameraResponse.java`

```java
package com.faceblogai.dto;

import com.faceblogai.domain.Camera;
import java.time.Instant;

public record CameraResponse(
        Long id,
        Long escolaId,
        String escolaNome,
        String nome,
        String endpointUrl,
        boolean ativo,
        Instant criadoEm) {

    public static CameraResponse from(Camera camera) {
        return new CameraResponse(
                camera.getId(),
                camera.getEscola().getId(),
                camera.getEscola().getNome(),
                camera.getNome(),
                camera.getEndpointUrl(),
                camera.isAtivo(),
                camera.getCriadoEm());
    }
}
```

---

#### `AlunoResponse.java`
**Caminho:** `src/main/java/com/faceblogai/dto/AlunoResponse.java`

```java
package com.faceblogai.dto;

import com.faceblogai.domain.Aluno;
import java.time.Instant;

public record AlunoResponse(
        Long id,
        String nome,
        String matricula,
        Instant criadoEm) {

    public static AlunoResponse from(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getMatricula(),
                aluno.getCriadoEm());
    }
}
```

---

#### `EscolaResponse.java`
**Caminho:** `src/main/java/com/faceblogai/dto/EscolaResponse.java`

```java
package com.faceblogai.dto;

import com.faceblogai.domain.Escola;
import java.time.Instant;

public record EscolaResponse(
        Long id,
        String nome,
        Instant criadoEm) {

    public static EscolaResponse from(Escola escola) {
        return new EscolaResponse(
                escola.getId(),
                escola.getNome(),
                escola.getCriadoEm());
    }
}
```

---

#### `EventoResponse.java`
**Caminho:** `src/main/java/com/faceblogai/dto/EventoResponse.java`

```java
package com.faceblogai.dto;

import com.faceblogai.domain.EventoMonitoramento;
import java.time.Instant;

public record EventoResponse(
        Long id,
        Long cameraId,
        String cameraNome,
        Long turmaId,
        String turmaNome,
        Long alunoId,
        String alunoNome,
        String tipoEvento,
        String detalhes,
        Instant criadoEm) {

    public static EventoResponse from(EventoMonitoramento e) {
        return new EventoResponse(
                e.getId(),
                e.getCamera().getId(),
                e.getCamera().getNome(),
                e.getTurma() == null ? null : e.getTurma().getId(),
                e.getTurma() == null ? null : e.getTurma().getNome(),
                e.getAluno() == null ? null : e.getAluno().getId(),
                e.getAluno() == null ? null : e.getAluno().getNome(),
                e.getTipoEvento() == null ? null : e.getTipoEvento().name(),
                e.getDetalhes(),
                e.getCriadoEm());
    }
}
```

---

### 1.2 Alterar os Controllers para usar os DTOs

Em **cada controller**, troque os retornos de entidade pelo DTO correspondente:

```java
// ANTES
public List<Camera> listarPorEscola(@PathVariable Long escolaId) {
    return cameraService.listarPorEscola(escolaId);
}

// DEPOIS
public List<CameraResponse> listarPorEscola(@PathVariable Long escolaId) {
    return cameraService.listarPorEscola(escolaId)
            .stream().map(CameraResponse::from).toList();
}
```

Repita o mesmo padrão para `AlunoController`, `EscolaController` e `EventoMonitoramentoController`.

---

## 2. `@PreAuthorize` nos Endpoints 🔴 Alta Prioridade

> **Problema:** Qualquer usuário autenticado (PROFESSOR, COORDENACAO ou ADMIN) pode deletar câmeras, criar escolas ou registrar eventos. Não há restrição por perfil nos endpoints sensíveis.

### 2.1 Habilitar Method Security

Adicione a anotação na classe `SecurityConfig.java`:

```java
// Adicione ANTES de @Configuration
@EnableMethodSecurity   // import: org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // ... resto da classe sem alteração
}
```

---

### 2.2 Proteger o `CameraController`

**Arquivo:** `src/main/java/com/faceblogai/controller/CameraController.java`

```java
// Adicionar no topo:
import org.springframework.security.access.prepost.PreAuthorize;

// criar():
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@PostMapping
public ResponseEntity<CameraResponse> criar(...) { ... }

// atualizar():
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@PutMapping("/{id}")
public ResponseEntity<CameraResponse> atualizar(...) { ... }

// deletar():
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletar(...) { ... }
```

---

### 2.3 Proteger o `EscolaController`

```java
// Criar e deletar escola: somente ADMIN
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
public ResponseEntity<EscolaResponse> criar(...) { ... }

@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletar(...) { ... }
```

---

### 2.4 Proteger o `AlunoController`

```java
// Deletar aluno: somente ADMIN ou COORDENACAO
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENACAO')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletar(...) { ... }
```

---

## 3. Testes 🔴 Alta Prioridade

> **Problema:** A pasta `src/test` está completamente vazia. Sem testes, qualquer refatoração pode quebrar silenciosamente o sistema.

### 3.1 Adicionar H2 no `pom.xml`

Dentro de `<dependencies>`, adicione:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

### 3.2 Criar `application-test.properties`

**Caminho:** `src/test/resources/application-test.properties`

```properties
# Banco em memória para testes
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.flyway.enabled=false

# JWT para testes (256 bits em base64)
jwt.secret-base64=dGVzdGVTZWNyZXRLZXlGYWNlTG9nQUlUZXN0ZVNlY3JldEtleUZhY2VMb2dBSQ==
```

---

### 3.3 `AuthServiceTest.java`

**Caminho:** `src/test/java/com/faceblogai/service/AuthServiceTest.java`

```java
package com.faceblogai.service;

import com.faceblogai.domain.PerfilUsuario;
import com.faceblogai.domain.Usuario;
import com.faceblogai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UsuarioRepository repo;
    @Mock PasswordEncoder encoder;
    @InjectMocks AuthService authService;

    @Test
    void autenticaComCredenciaisValidas() {
        var user = new Usuario("Admin", "a@a.com", "hash", PerfilUsuario.ADMIN);
        when(repo.findByEmail("a@a.com")).thenReturn(Optional.of(user));
        when(encoder.matches("senha123", "hash")).thenReturn(true);

        assertThat(authService.authenticate("a@a.com", "senha123")).isPresent();
    }

    @Test
    void retornaVazioComSenhaErrada() {
        var user = new Usuario("Admin", "a@a.com", "hash", PerfilUsuario.ADMIN);
        when(repo.findByEmail("a@a.com")).thenReturn(Optional.of(user));
        when(encoder.matches("errada", "hash")).thenReturn(false);

        assertThat(authService.authenticate("a@a.com", "errada")).isEmpty();
    }

    @Test
    void retornaVazioSeUsuarioNaoExiste() {
        when(repo.findByEmail("x@x.com")).thenReturn(Optional.empty());

        assertThat(authService.authenticate("x@x.com", "qualquer")).isEmpty();
    }
}
```

---

### 3.4 `JwtServiceTest.java`

**Caminho:** `src/test/java/com/faceblogai/service/JwtServiceTest.java`

```java
package com.faceblogai.service;

import com.faceblogai.domain.PerfilUsuario;
import com.faceblogai.domain.Usuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
        "dGVzdGVTZWNyZXRLZXlGYWNlTG9nQUlUZXN0ZVNlY3JldEtleUZhY2VMb2dBSQ==";

    @Test
    void geraEValidaToken() {
        var service = new JwtService(SECRET);
        var user = new Usuario("Admin", "a@a.com", "hash", PerfilUsuario.ADMIN);

        String token = service.generateToken(user);
        var claims = service.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo("a@a.com");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }

    @Test
    void lancaExcecaoSemSecret() {
        var service = new JwtService("");
        var user = new Usuario("Admin", "a@a.com", "hash", PerfilUsuario.ADMIN);

        assertThatThrownBy(() -> service.generateToken(user))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("JWT_SECRET_BASE64");
    }
}
```

---

### 3.5 `AuthControllerIT.java`

**Caminho:** `src/test/java/com/faceblogai/controller/AuthControllerIT.java`

```java
package com.faceblogai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired MockMvc mvc;

    @Test
    void loginComCredenciaisValidas() throws Exception {
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"admin@facelogai.local","password":"admin123"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"admin@facelogai.local","password":"errada"}
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointSemTokenRetorna401() throws Exception {
        mvc.perform(post("/api/cameras"))
            .andExpect(status().isUnauthorized());
    }
}
```

---

## 4. Paginação nas Listagens 🟡 Média Prioridade

> **Problema:** `AlunoService.listarTodos()` usa `findAll()` sem limite. Com muitos registros, carrega tudo na memória de uma vez.

### 4.1 Alterar `AlunoController`

```java
// ANTES
@GetMapping
public List<Aluno> listarTodos() {
    return alunoService.listarTodos();
}

// DEPOIS
@GetMapping
public Page<AlunoResponse> listarTodos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    return alunoService.listarPaginado(page, size).map(AlunoResponse::from);
}
```

---

### 4.2 Alterar `AlunoService`

```java
// Adicionar imports no topo:
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

// Adicionar método na classe:
public Page<Aluno> listarPaginado(int page, int size) {
    int safeSize = Math.min(size, 100);
    return alunoRepository.findAll(
        PageRequest.of(page, safeSize, Sort.by("nome")));
}
```

---

## 5. `@Transactional` nos Services 🟡 Média Prioridade

> **Problema:** Métodos de escrita não são transacionais. Uma falha parcial pode deixar o banco em estado inconsistente.

Adicione `@Transactional` nos métodos de escrita de cada service. Exemplo para `VinculoService`:

```java
// Adicionar import no topo:
import org.springframework.transaction.annotation.Transactional;

// Anotar todos os métodos que escrevem no banco:
@Transactional
public TurmaAluno vincularAlunoEmTurma(Long turmaId, Long alunoId) { ... }

@Transactional
public void desvincularAlunoDeTurma(Long turmaId, Long alunoId) { ... }

@Transactional
public CameraTurma vincularCameraEmTurma(Long turmaId, Long cameraId) { ... }

@Transactional
public void desvincularCameraDeTurma(Long turmaId, Long cameraId) { ... }
```

Repita o mesmo padrão para `AlunoService` (criar, atualizar, deletar), `CameraService` e `TurmaService`.

---

## 6. Validação do JWT Secret no Startup 🟡 Média Prioridade

> **Problema:** O sistema inicia normalmente mesmo sem `JWT_SECRET_BASE64` configurado. O erro só aparece no primeiro login.

**Arquivo:** `src/main/java/com/faceblogai/service/JwtService.java`

```java
// Adicionar import no topo:
import jakarta.annotation.PostConstruct;

// Adicionar método na classe JwtService:
@PostConstruct
void validarConfiguracao() {
    if (secretKeyBase64 == null || secretKeyBase64.isBlank()) {
        throw new IllegalStateException(
            "JWT_SECRET_BASE64 é obrigatório. " +
            "Gere um com: openssl rand -base64 32");
    }
}
```

---

## 7. Docker Compose 🟢 Baixa Prioridade

> Não altera nenhuma classe Java. Apenas facilita o setup local sem instalar o MySQL manualmente.

### 7.1 `docker-compose.yml`

**Caminho:** raiz do projeto (ao lado do `pom.xml`)

```yaml
services:
  db:
    image: mysql:8.0
    container_name: facelogai_db
    environment:
      MYSQL_DATABASE: facelogai
      MYSQL_USER: facelogai
      MYSQL_PASSWORD: facelogai
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
    volumes:
      - facelogai_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      retries: 5

volumes:
  facelogai_data:
```

```bash
# Subir o banco:
docker compose up -d

# Parar:
docker compose down
```

---

### 7.2 Gerar o JWT secret para desenvolvimento

```bash
# Linux/Mac:
openssl rand -base64 32

# Ou com Node.js:
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

Cole o resultado na variável de ambiente `JWT_SECRET_BASE64` antes de rodar a aplicação.

---

## Checklist de Implementação

Ordem sugerida para implementar no Cursor sem quebrar o projeto:

- [x] **Passo 1** — Adicionar H2 no `pom.xml` e criar `application-test.properties`
- [x] **Passo 2** — Criar os 4 arquivos de DTO (`CameraResponse`, `AlunoResponse`, `EscolaResponse`, `EventoResponse`)
- [x] **Passo 3** — Atualizar os controllers para usar os DTOs
- [x] **Passo 4** — Adicionar `@EnableMethodSecurity` na `SecurityConfig`
- [x] **Passo 5** — Adicionar `@PreAuthorize` nos endpoints sensíveis
- [x] **Passo 6** — Adicionar `@PostConstruct` no `JwtService`
- [x] **Passo 7** — Adicionar `@Transactional` nos services
- [x] **Passo 8** — Escrever os 3 arquivos de teste
- [x] **Passo 9** — Adicionar paginação no `AlunoController` e `AlunoService`
- [x] **Passo 10** — Criar o `docker-compose.yml`
