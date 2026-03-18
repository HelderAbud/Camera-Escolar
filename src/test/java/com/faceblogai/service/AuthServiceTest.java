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

    @Mock
    UsuarioRepository repo;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    AuthService authService;

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

