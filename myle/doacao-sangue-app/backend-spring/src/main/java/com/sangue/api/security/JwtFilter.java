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

// Filtro executado uma vez por requisição para autenticar com base no token JWT
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

        // Recupera o token do cabeçalho Authorization
        String authHeader = request.getHeader("Authorization");

        // Verifica se o token está presente e começa com "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            try {
                // Extrai o email de dentro do token
                String email = jwtUtil.extrairEmail(token);

                // Busca o usuário correspondente no banco
                Usuario usuario = usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado com esse token"));

                // Cria o objeto de autenticação e coloca no contexto do Spring
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(usuario, null, null);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                // Se der erro ao validar, ignora o token e segue sem autenticação
                System.out.println("Erro no filtro JWT: " + e.getMessage());
            }
        }

        // Continua o fluxo da requisição
        filterChain.doFilter(request, response);
    }
}
