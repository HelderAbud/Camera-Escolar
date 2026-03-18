package com.faceblogai.service;

import com.faceblogai.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final String secretKeyBase64;

    public JwtService(@Value("${jwt.secret-base64:}") String secretKeyBase64) {
        this.secretKeyBase64 = secretKeyBase64;
    }

    @PostConstruct
    void validarConfiguracao() {
        if (secretKeyBase64 == null || secretKeyBase64.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET_BASE64 é obrigatório. Gera um com: openssl rand -base64 32");
        }

        // Validação extra para falhar cedo caso o secret esteja mal configurado.
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
            Keys.hmacShaKeyFor(keyBytes); // garante tamanho compatível com HS256
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "JWT_SECRET_BASE64 inválido (não é base64 válido ou tamanho incompatível).",
                    ex);
        }
    }

    private Key signingKey() {
        if (secretKeyBase64 == null || secretKeyBase64.isBlank()) {
            throw new IllegalStateException("JWT secret não configurado. Defina JWT_SECRET_BASE64.");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("role", usuario.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
