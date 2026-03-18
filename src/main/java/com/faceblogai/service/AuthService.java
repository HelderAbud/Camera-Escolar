package com.faceblogai.service;

import com.faceblogai.domain.Usuario;
import com.faceblogai.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> authenticate(String email, String rawPassword) {
        return usuarioRepository
                .findByEmail(email)
                .filter(usuario -> passwordEncoder.matches(rawPassword, usuario.getSenhaHash()));
    }
}
