package com.sangue.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utilitário JWT (JJWT 0.11.5) — compatível com Java 17.
 * - Gera tokens (HS256)
 * - Extrai subject (email)
 * - Valida expiração/assinatura
 */
@Component
public class JwtUtil {

    private final Key key;          // chave HMAC derivada do secret
    private final long expiration;  // em milissegundos

    // injeta valores do application.properties
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        // HS256 exige chave >= 256 bits (32 bytes). Garanta secret grande o suficiente.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * Gera token com subject = email.
     */
    public String gerarToken(String email) {
        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(agora)
                .setExpiration(expira)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o email (subject) do token.
     * Lança exceção se inválido/expirado.
     */
    public String extrairEmail(String token) {
        return getClaims(token).getBody().getSubject();
    }

    /**
     * Verifica se o token é válido (assinatura + expiração).
     * Retorna true/false sem lançar exceção.
     */
    public boolean tokenValido(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Parser centralizado para claims.
     * Se inválido/expirado, lança JwtException.
     */
    private Jws<Claims> getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
