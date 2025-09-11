package com.sangue.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utilitário JWT (JJWT 0.11.5) — compatível com Java 17.
 * Responsável por gerar, validar e extrair dados do token.
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /** Gera token com subject = email */
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

    /** Extrai o email (subject) do token */
    public String extrairEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Retorna tempo de expiração em ms */
    public long getExpirationMillis() {
        return expiration;
    }

    /** Valida assinatura e expiração do token */
    public boolean tokenValido(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
