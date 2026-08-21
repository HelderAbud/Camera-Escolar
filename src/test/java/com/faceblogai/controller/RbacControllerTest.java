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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/rbac-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RbacControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;

    private String tokenAdmin;
    private String tokenCoordenacao;
    private String tokenProfessor;

    @BeforeEach
    void setup() {
        tokenAdmin =
                jwtService.generateToken(
                        new Usuario("Admin", "admin@test.local", "hash", PerfilUsuario.ADMIN));
        tokenCoordenacao =
                jwtService.generateToken(
                        new Usuario(
                                "Coord", "coord@test.local", "hash", PerfilUsuario.COORDENACAO));
        tokenProfessor =
                jwtService.generateToken(
                        new Usuario("Professor", "prof@test.local", "hash", PerfilUsuario.PROFESSOR));
    }

    @Test
    void professorNaoPodeAtualizarEscolaRetorna403() throws Exception {
        mockMvc.perform(
                        put("/api/escolas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenProfessor)
                                .content("{\"nome\":\"Escola Alterada\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void coordenacaoPodeAtualizarEscola() throws Exception {
        mockMvc.perform(
                        put("/api/escolas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenCoordenacao)
                                .content("{\"nome\":\"Escola Coord\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Escola Coord"));
    }

    @Test
    void professorNaoPodeCriarAlunoRetorna403() throws Exception {
        mockMvc.perform(
                        post("/api/alunos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenProfessor)
                                .content("{\"nome\":\"Novo\",\"matricula\":\"MATR-PROF-X\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void professorNaoPodeAtualizarAlunoRetorna403() throws Exception {
        mockMvc.perform(
                        put("/api/alunos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenProfessor)
                                .content("{\"nome\":\"Aluno Editado\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void professorNaoPodeVincularAlunoRetorna403() throws Exception {
        mockMvc.perform(
                        post("/api/turmas/1/alunos/1")
                                .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isForbidden());
    }

    @Test
    void professorPodeListarAlunosDaTurma() throws Exception {
        mockMvc.perform(
                        get("/api/turmas/1/alunos")
                                .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk());
    }

    @Test
    void professorNaoPodeRegistrarEventoRetorna403() throws Exception {
        mockMvc.perform(
                        post("/api/eventos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenProfessor)
                                .content(
                                        """
                                        {
                                          "cameraId": 1,
                                          "tipoEvento": "CAMERA_ONLINE",
                                          "detalhes": "nao deve passar"
                                        }
                                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    void coordenacaoPodeRegistrarEvento() throws Exception {
        mockMvc.perform(
                        post("/api/eventos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenCoordenacao)
                                .content(
                                        """
                                        {
                                          "cameraId": 1,
                                          "tipoEvento": "CAMERA_ONLINE",
                                          "detalhes": "coord ok"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoEvento").value("CAMERA_ONLINE"));
    }

    @Test
    void professorPodeLerEventos() throws Exception {
        mockMvc.perform(get("/api/eventos").header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk());
    }

    @Test
    void adminPodeCriarAluno() throws Exception {
        mockMvc.perform(
                        post("/api/alunos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenAdmin)
                                .content("{\"nome\":\"Aluno Admin\",\"matricula\":\"MATR-ADM-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("MATR-ADM-1"));
    }
}
