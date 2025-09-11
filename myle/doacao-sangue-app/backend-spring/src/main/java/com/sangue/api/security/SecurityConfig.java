package com.sangue.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

/**
 * Configuração central de segurança (Spring Security 6).
 * - Stateless (JWT)
 * - CORS liberado para dev
 * - Rotas públicas x privadas
 * - Filtro JWT antes do UsernamePasswordAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Cadeia de filtros e regras de autorização.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // API REST: sem CSRF
                .csrf(csrf -> csrf.disable())

                // CORS para permitir chamadas do front local
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Stateless: sem sessão no servidor
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Regras de autorização
                .authorizeHttpRequests(auth -> auth
                        // Libera autenticação/registro/refresh etc.
                        .requestMatchers("/auth/**").permitAll()

                        // Libera arquivos estáticos (se servir pelo mesmo backend)
                        .requestMatchers(
                                "/", "/index.html",
                                "/favicon.ico",
                                "/css/**", "/js/**", "/images/**",
                                "/static/**"
                        ).permitAll()

                        // (opcional) healthcheck
                        .requestMatchers("/actuator/health").permitAll()

                        // Opcional: liberar OPTIONS (preflight CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Qualquer outra rota requer token JWT válido
                        .anyRequest().authenticated()
                )

                // Retorno 401 quando não autenticado em rota privada
                .httpBasic(Customizer.withDefaults());

        // Insere o filtro JWT antes do filtro padrão de autenticação
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS para desenvolvimento.
     * Ajuste origins conforme sua porta do front (ex.: 5173/Vite, 3000/Next/React).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:8080",
                "http://localhost:63342"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /**
     * Encoder de senhas (BCrypt).
     * Use ao salvar/validar senha do usuário.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager (pode ser útil em fluxos de autenticação personalizados).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
