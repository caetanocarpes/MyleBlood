package com.sangue.api.controller;

import com.sangue.api.dto.LoginDTO;
import com.sangue.api.dto.UsuarioDTO;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.UsuarioRepository;
import com.sangue.api.security.JwtUtil;
import com.sangue.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Endpoints de autenticação:
 * - POST /auth/register  → cria usuário
 * - POST /auth/login     → gera JWT
 * - GET  /auth/me        → retorna dados do usuário autenticado
 *
 * Observações:
 * - O JwtFilter já valida o token e coloca o Usuario no SecurityContext.
 * - Este controller usa PasswordEncoder (BCrypt) para validar as senhas.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // configurado no SecurityConfig

    /**
     * Modelo de resposta do login.
     * Mantém o payload estável e legível para o front.
     */
    public record LoginResponse(
            String token,
            long   expiresAt, // epoch millis (quando expira)
            Map<String, Object> usuario // dados essenciais p/ front
    ) {}

    /**
     * Registro de usuário.
     * - Valida o UsuarioDTO com Bean Validation.
     * - Usa o UsuarioService para salvar com senha criptografada.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioDTO dto) {
        try {
            usuarioService.cadastrar(dto);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (RuntimeException e) {
            // Exemplo: email já existente, CPF duplicado etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Login:
     * - Valida email/senha
     * - Gera token JWT
     * - Retorna também expiresAt e dados essenciais do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElse(null);

        // Valida credenciais
        if (usuario == null || !passwordEncoder.matches(loginDTO.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
        }

        // Gera token JWT
        String token = jwtUtil.gerarToken(usuario.getEmail());

        // Calcula expiração direto pela config (opção 1)
        long expiresAt = System.currentTimeMillis() + jwtUtil.getExpirationMillis();

        // Monta mapa com dados essenciais do usuário
        Map<String, Object> usr = new HashMap<>();
        usr.put("id", usuario.getId());
        usr.put("nome", usuario.getNome());
        usr.put("email", usuario.getEmail());
        usr.put("cpf", usuario.getCpf());
        usr.put("tipoSanguineo", usuario.getTipoSanguineo());
        usr.put("pesoKg", usuario.getPesoKg());
        usr.put("alturaCm", usuario.getAlturaCm());

        return ResponseEntity.ok(new LoginResponse(token, expiresAt, usr));
    }

    /**
     * Retorna dados do usuário autenticado com base no SecurityContext.
     * - O JwtFilter já setou o principal como Usuario.
     * - Se não autenticado, retorna 401.
     */
    @GetMapping("/me")
    public ResponseEntity<?> perfil() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
        }

        Map<String, Object> dados = new HashMap<>();
        dados.put("id", usuario.getId());
        dados.put("nome", usuario.getNome());
        dados.put("email", usuario.getEmail());
        dados.put("cpf", usuario.getCpf());
        dados.put("dataNascimento", usuario.getDataNascimento());
        dados.put("tipoSanguineo", usuario.getTipoSanguineo());
        dados.put("pesoKg", usuario.getPesoKg());
        dados.put("alturaCm", usuario.getAlturaCm());

        return ResponseEntity.ok(dados);
    }
}
