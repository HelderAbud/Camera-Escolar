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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/rbac-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TurmaControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;

    private String tokenAdmin;
    private String tokenProfessor;

    @BeforeEach
    void setup() {
        tokenAdmin =
                jwtService.generateToken(
                        new Usuario("Admin", "admin@test.local", "hash", PerfilUsuario.ADMIN));
        tokenProfessor =
                jwtService.generateToken(
                        new Usuario("Professor", "prof@test.local", "hash", PerfilUsuario.PROFESSOR));
    }

    @Test
    void listarPorEscolaRetornaDtoSemEntidadeJpa() throws Exception {
        mockMvc.perform(
                        get("/api/turmas/escola/1")
                                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].escolaId").value(1))
                .andExpect(jsonPath("$[0].escolaNome").value("Escola RBAC"))
                .andExpect(jsonPath("$[0].nome").value("Turma RBAC"))
                .andExpect(jsonPath("$[0].serie").value("1A"))
                .andExpect(jsonPath("$[0].escola").doesNotExist());
    }

    @Test
    void adminVinculaAlunoEListaDto() throws Exception {
        mockMvc.perform(
                        post("/api/turmas/1/alunos/1")
                                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turmaId").value(1))
                .andExpect(jsonPath("$.alunoId").value(1))
                .andExpect(jsonPath("$.alunoNome").value("Aluno RBAC"))
                .andExpect(jsonPath("$.aluno").doesNotExist());

        mockMvc.perform(
                        get("/api/turmas/1/alunos")
                                .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].alunoNome").value("Aluno RBAC"));
    }

    @Test
    void adminVinculaCameraEListaDto() throws Exception {
        mockMvc.perform(
                        post("/api/turmas/1/cameras/1")
                                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turmaId").value(1))
                .andExpect(jsonPath("$.cameraId").value(1))
                .andExpect(jsonPath("$.cameraNome").value("Camera RBAC"));

        mockMvc.perform(
                        get("/api/turmas/1/cameras")
                                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cameraNome").value("Camera RBAC"));
    }

    @Test
    void professorNaoPodeCriarTurmaRetorna403() throws Exception {
        mockMvc.perform(
                        post("/api/turmas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenProfessor)
                                .content(
                                        """
                                        {"escolaId": 1, "nome": "Turma Prof", "serie": "2A"}
                                        """))
                .andExpect(status().isForbidden());
    }
}
