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
@Sql(scripts = "classpath:sql/camera-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CameraControllerIT {

    @Autowired MockMvc mockMvc;

    @Autowired JwtService jwtService;

    private String tokenAdmin;
    private String tokenProfessor;

    private static final long ESCOLA_ID = 1L;

    @BeforeEach
    void setup() {
        tokenAdmin =
                jwtService.generateToken(
                        new Usuario("Admin", "admin@test.local", "hash", PerfilUsuario.ADMIN));
        tokenProfessor =
                jwtService.generateToken(
                        new Usuario("Professor", "prof@test.local", "hash", PerfilUsuario.PROFESSOR));
    }

    private String jsonCriarCamera(String nome, String endpointUrl) {
        return """
                {
                  "escolaId": %d,
                  "nome": "%s",
                  "endpointUrl": "%s"
                }
                """.formatted(ESCOLA_ID, nome, endpointUrl);
    }

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(
                        post("/api/cameras")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCriarCamera("Camera X", "http://example.com/camX")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminPodeCriarCameraERetornaDto() throws Exception {
        mockMvc.perform(
                        post("/api/cameras")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenAdmin)
                                .content(jsonCriarCamera("Camera X", "http://example.com/camX")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.escolaId").value(ESCOLA_ID))
                .andExpect(jsonPath("$.nome").value("Camera X"))
                .andExpect(jsonPath("$.endpointUrl").value("http://example.com/camX"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void listarPorEscolaRetorna200() throws Exception {
        mockMvc.perform(
                        get("/api/cameras/escola/" + ESCOLA_ID)
                                .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].escolaId").value(ESCOLA_ID))
                .andExpect(jsonPath("$[0].nome").value("Camera SQL"))
                .andExpect(jsonPath("$[0].endpointUrl").value("http://example.com/cam-sql"))
                .andExpect(jsonPath("$[0].ativo").value(true));
    }

    @Test
    void corpoInvalidoRetorna400ErroDeValidacao() throws Exception {
        mockMvc.perform(post("/api/cameras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].field").exists());
    }

    @Test
    void professorNaoPodeCriarCameraRetorna403() throws Exception {
        mockMvc.perform(
                        post("/api/cameras")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + tokenProfessor)
                                .content(jsonCriarCamera("Camera X", "http://example.com/camX")))
                .andExpect(status().isForbidden());
    }
}

