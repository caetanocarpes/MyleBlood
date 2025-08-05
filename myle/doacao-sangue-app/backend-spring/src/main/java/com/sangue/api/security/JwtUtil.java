package com.sangue.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Classe utilitária para gerar e validar tokens JWT
 */
@Component
public class JwtUtil {

    private static final String SECRET = "chave-secreta-super-segura"; // Trocar em produção
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24h

    // Gera um token para o email
    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // Extrai o email do token
    public String extrairEmail(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
