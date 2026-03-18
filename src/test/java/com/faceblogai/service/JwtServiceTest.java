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
        JwtService service = new JwtService(SECRET);
        Usuario user = new Usuario("Admin", "a@a.com", "hash", PerfilUsuario.ADMIN);

        String token = service.generateToken(user);
        var claims = service.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo("a@a.com");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }

    @Test
    void lancaExcecaoSemSecret() {
        JwtService service = new JwtService("");
        Usuario user = new Usuario("Admin", "a@a.com", "hash", PerfilUsuario.ADMIN);

        assertThatThrownBy(() -> service.generateToken(user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT secret");
    }
}

