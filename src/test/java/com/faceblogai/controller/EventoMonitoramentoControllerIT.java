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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/evento-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EventoMonitoramentoControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;

    private String tokenAdmin;

    private static final long CAMERA_ID = 1L;
    private static final long TURMA_ID = 1L;
    private static final long ALUNO_ID = 1L;

    @BeforeEach
    void setup() {
        tokenAdmin =
                jwtService.generateToken(
                        new Usuario("Admin", "admin@test.local", "hash", PerfilUsuario.ADMIN));
    }

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cameraId": %d,
                                  "turmaId": null,
                                  "alunoId": null,
                                  "tipoEvento": "AUSENCIA_DETECTADA",
                                  "detalhes": "detalhes"
                                }
                                """.formatted(CAMERA_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminRegistraEventoERetornaDto() throws Exception {
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content("""
                                {
                                  "cameraId": %d,
                                  "turmaId": %d,
                                  "alunoId": %d,
                                  "tipoEvento": "PRESENCA_DETECTADA",
                                  "detalhes": "detalhes (SQL POST)"
                                }
                                """.formatted(CAMERA_ID, TURMA_ID, ALUNO_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cameraId").value(CAMERA_ID))
                .andExpect(jsonPath("$.turmaId").value(TURMA_ID))
                .andExpect(jsonPath("$.alunoId").value(ALUNO_ID))
                .andExpect(jsonPath("$.tipoEvento").value("PRESENCA_DETECTADA"))
                .andExpect(jsonPath("$.detalhes").value("detalhes (SQL POST)"));
    }

    @Test
    void listarRetorna200() throws Exception {
        mockMvc.perform(
                        get("/api/eventos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].cameraId").value(CAMERA_ID))
                .andExpect(jsonPath("$.content[0].turmaId").value(TURMA_ID))
                .andExpect(jsonPath("$.content[0].alunoId").value(ALUNO_ID))
                .andExpect(jsonPath("$.content[0].tipoEvento").value("AUSENCIA_DETECTADA"))
                .andExpect(jsonPath("$.content[0].detalhes").value("Evento inicial (SQL)"));
    }

    @Test
    void corpoInvalidoRetorna400ErroDeValidacao() throws Exception {
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].field").exists());
    }

    @Test
    void tipoEventoInvalidoRetorna400() throws Exception {
        mockMvc.perform(
                        post("/api/eventos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenAdmin)
                                .content(
                                        """
                                        {
                                          "cameraId": %d,
                                          "turmaId": %d,
                                          "alunoId": %d,
                                          "tipoEvento": "INVALIDO",
                                          "detalhes": "detalhes invalido"
                                        }
                                        """
                                                .formatted(CAMERA_ID, TURMA_ID, ALUNO_ID)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Parâmetro inválido"))
                .andExpect(jsonPath("$.detail").exists());
    }
}

