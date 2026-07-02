package com.faceblogai.config;

import com.faceblogai.domain.PerfilUsuario;
import com.faceblogai.domain.Usuario;
import com.faceblogai.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class DevAdminSeeder implements ApplicationRunner {

    private final DevAdminSeedProperties properties;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DevAdminSeeder(
            DevAdminSeedProperties properties,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            return;
        }

        if (!StringUtils.hasText(properties.getPassword())) {
            throw new IllegalStateException(
                    "facelogai.seed-admin.password deve ser configurado quando o seed admin estiver habilitado.");
        }

        String passwordHash = passwordEncoder.encode(properties.getPassword());
        usuarioRepository
                .findByEmail(properties.getEmail())
                .ifPresentOrElse(
                        usuario -> updateAdmin(usuario, passwordHash),
                        () -> createAdmin(passwordHash));
    }

    private void updateAdmin(Usuario usuario, String passwordHash) {
        usuario.setSenhaHash(passwordHash);
        usuario.setRole(PerfilUsuario.ADMIN);
        usuarioRepository.save(usuario);
    }

    private void createAdmin(String passwordHash) {
        Usuario admin = new Usuario(
                properties.getName(),
                properties.getEmail(),
                passwordHash,
                PerfilUsuario.ADMIN);
        usuarioRepository.save(admin);
    }
}
