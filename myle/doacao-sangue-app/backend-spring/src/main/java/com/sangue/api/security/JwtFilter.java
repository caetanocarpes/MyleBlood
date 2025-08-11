package com.sangue.api.security;

import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT executado uma vez por requisição.
 * Agora ele IGNORA rotas públicas (ex.: /auth/**), para não barrar o login.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // IMPORTANTE: não interceptar rotas públicas (login, registro, etc.)
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lê o Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Extrai e valida informações do token
                String email = jwtUtil.extrairEmail(token);

                // Carrega o usuário para colocar no contexto de segurança
                Usuario usuario = usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado para o token"));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(usuario, null, null);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Marca a requisição como autenticada
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Se token inválido/expirado, segue sem autenticar (as rotas privadas negarão o acesso)
                System.out.println("Erro no filtro JWT: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
